package com.br.shizen.certificateemitter.services;

import com.br.shizen.certificateemitter.dto.CertificateEmissionResult;
import com.br.shizen.certificateemitter.dto.CertificateImportRequest;
import com.br.shizen.certificateemitter.dto.EmailSendResult;
import com.br.shizen.certificateemitter.dto.EmailTemplate;
import com.br.shizen.certificateemitter.dto.ImportError;
import com.br.shizen.certificateemitter.dto.answersheet.AnswerSheetImportResult;
import com.br.shizen.certificateemitter.dto.answersheet.AnswerSheetResult;
import com.br.shizen.certificateemitter.dto.certificate.CertificateIssueStatus;
import com.br.shizen.certificateemitter.services.answersheet.AnswerSheetImportService;
import com.br.shizen.certificateemitter.services.answersheet.AnswerSheetValidationException;
import com.br.shizen.certificateemitter.services.certificate.CertificateTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificateEmissionService {
    private final AnswerSheetImportService answerSheetImportService;
    private final CertificateService certificateService;
    private final EmailTemplateService emailTemplateService;
    private final EmailSenderService emailSenderService;

    public CertificateEmissionResult emit(CertificateImportRequest request) throws IOException {
        try {
            AnswerSheetImportResult answerImport =
                    answerSheetImportService.importAnswerSheet(request.formAnswersName());

            CertificateTemplate certificateTemplate =
                    certificateService.loadTemplate(request.certificateTemplate());

            EmailTemplate emailTemplate =
                    emailTemplateService.loadTemplate(request.emailTemplateName());

            List<CertificateIssueStatus> certificateStatuses = certificateService.generateCertificates(
                    answerImport,
                    certificateTemplate
            );

            EmailSendResult emailSendResult = emailSenderService.sendCertificates(certificateStatuses, emailTemplate);
            return combine(answerImport, certificateStatuses, emailSendResult);
        } catch (AnswerSheetValidationException e) {
            return CertificateEmissionResult.failed(e.getRowsRead(), e.getErrors());
        }
    }

    private CertificateEmissionResult combine(
            AnswerSheetImportResult answerImport,
            List<CertificateIssueStatus> certificateStatuses,
            EmailSendResult emailSendResult
    ) {
        List<AnswerSheetResult> answers = answerImport.getAnswers() == null
                ? Collections.emptyList()
                : answerImport.getAnswers();
        int rowsRead = answers.size();
        int certificatesGenerated = (int) certificateStatuses.stream()
                .filter(CertificateIssueStatus::isIssued)
                .count();
        int responsesCreated = answers.stream()
                .map(AnswerSheetResult::getResponses)
                .mapToInt(responses -> responses == null ? 0 : responses.size())
                .sum();

        List<ImportError> errors = new ArrayList<>(emailSendResult.errors());
        return new CertificateEmissionResult(
                rowsRead,
                rowsRead,
                0,
                0,
                0,
                responsesCreated,
                0,
                certificatesGenerated,
                errors
        );
    }
}
