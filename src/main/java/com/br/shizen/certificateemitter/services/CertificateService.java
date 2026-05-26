package com.br.shizen.certificateemitter.services;

import com.br.shizen.certificateemitter.dto.answersheet.AnswerSheetImportResult;
import com.br.shizen.certificateemitter.dto.answersheet.AnswerSheetResult;
import com.br.shizen.certificateemitter.dto.certificate.CertificateIssueStatus;
import com.br.shizen.certificateemitter.services.certificate.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificateService {
    @Setter
    @Getter
    private String certificateTemplateFileName;

    private final CertificateProvider certificateProvider;
    private final CertificateRenderer certificateRenderer;
    private final DocumentToPdfConverter documentToPdfConverter;
    private final CertificatePersistenceService certificatePersistenceService;

    public CertificateTemplate loadTemplate(String templateName) throws IOException {
        return certificateProvider.download(templateName);
    }

    public List<CertificateIssueStatus> generateCertificates(
            AnswerSheetImportResult answerImport,
            CertificateTemplate certificateTemplate
    ) throws IOException {
        List<CertificateIssueStatus> certificateIssueStatusList = new ArrayList<>();

        for (AnswerSheetResult answer : answerImport.getAnswers()) {
            if (!answer.getTake().isApproved()) {
                // skip issue certificate for students that didn't pass the quiz
                continue;
            }
            CertificateIssueStatus certificateIssueStatus = new CertificateIssueStatus();
            certificateIssueStatus.setStudent(answer.getStudent());
            certificatePersistenceService.persistIssuedCertificate(answer.getStudent(), false);
            File renderedCertificate = certificateRenderer.render(certificateTemplate, answer);
            certificateIssueStatus.setRenderedCertificate(renderedCertificate);
            File renderedPdfCertificate = documentToPdfConverter.convert(renderedCertificate);
            certificateIssueStatus.setRenderedPdfCertificate(renderedPdfCertificate);
            certificatePersistenceService.persistIssuedCertificate(answer.getStudent(), true);
            certificateIssueStatus.setIssued(true);
            certificateIssueStatusList.add(certificateIssueStatus);
        }
        return certificateIssueStatusList;
    }
}
