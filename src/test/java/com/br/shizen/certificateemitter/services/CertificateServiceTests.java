package com.br.shizen.certificateemitter.services;

import com.br.shizen.certificateemitter.dto.answersheet.AnswerSheetImportResult;
import com.br.shizen.certificateemitter.dto.answersheet.AnswerSheetResult;
import com.br.shizen.certificateemitter.dto.certificate.CertificateIssueStatus;
import com.br.shizen.certificateemitter.entity.Student;
import com.br.shizen.certificateemitter.entity.Take;
import com.br.shizen.certificateemitter.services.certificate.CertificatePersistenceService;
import com.br.shizen.certificateemitter.services.certificate.CertificateProvider;
import com.br.shizen.certificateemitter.services.certificate.CertificateRenderer;
import com.br.shizen.certificateemitter.services.certificate.CertificateTemplate;
import com.br.shizen.certificateemitter.services.certificate.DocumentToPdfConverter;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CertificateServiceTests {

    @Test
    void generateCertificatesConvertsRenderedCertificateBeforePersistingIssuedCertificate() throws Exception {
        CertificateProvider certificateProvider = mock(CertificateProvider.class);
        CertificateRenderer certificateRenderer = mock(CertificateRenderer.class);
        DocumentToPdfConverter documentToPdfConverter = mock(DocumentToPdfConverter.class);
        CertificatePersistenceService certificatePersistenceService = mock(CertificatePersistenceService.class);
        CertificateService service = new CertificateService(
                certificateProvider,
                certificateRenderer,
                documentToPdfConverter,
                certificatePersistenceService
        );

        CertificateTemplate template = new CertificateTemplate("template.docx", "template.docx", "file-id");
        AnswerSheetResult answer = approvedAnswer();
        AnswerSheetImportResult importResult = new AnswerSheetImportResult();
        importResult.setAnswers(List.of(answer));
        File renderedCertificate = new File("student.docx");
        File renderedPdfCertificate = new File("student.pdf");

        when(certificateRenderer.render(template, answer)).thenReturn(renderedCertificate);
        when(documentToPdfConverter.convert(renderedCertificate)).thenReturn(renderedPdfCertificate);

        List<CertificateIssueStatus> result = service.generateCertificates(importResult, template);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getRenderedCertificate()).isEqualTo(renderedCertificate);
        assertThat(result.getFirst().getRenderedPdfCertificate()).isEqualTo(renderedPdfCertificate);
        assertThat(result.getFirst().isIssued()).isTrue();

        var inOrder = inOrder(certificatePersistenceService, certificateRenderer, documentToPdfConverter);
        inOrder.verify(certificatePersistenceService).persistIssuedCertificate(answer.getStudent(), false);
        inOrder.verify(certificateRenderer).render(template, answer);
        inOrder.verify(documentToPdfConverter).convert(renderedCertificate);
        inOrder.verify(certificatePersistenceService).persistIssuedCertificate(answer.getStudent(), true);
    }

    @Test
    void generateCertificatesDoesNotPersistIssuedCertificateWhenPdfConversionFails() throws Exception {
        CertificateProvider certificateProvider = mock(CertificateProvider.class);
        CertificateRenderer certificateRenderer = mock(CertificateRenderer.class);
        DocumentToPdfConverter documentToPdfConverter = mock(DocumentToPdfConverter.class);
        CertificatePersistenceService certificatePersistenceService = mock(CertificatePersistenceService.class);
        CertificateService service = new CertificateService(
                certificateProvider,
                certificateRenderer,
                documentToPdfConverter,
                certificatePersistenceService
        );

        CertificateTemplate template = new CertificateTemplate("template.docx", "template.docx", "file-id");
        AnswerSheetResult answer = approvedAnswer();
        AnswerSheetImportResult importResult = new AnswerSheetImportResult();
        importResult.setAnswers(List.of(answer));
        File renderedCertificate = new File("student.docx");

        when(certificateRenderer.render(template, answer)).thenReturn(renderedCertificate);
        when(documentToPdfConverter.convert(renderedCertificate)).thenThrow(new IOException("conversion failed"));

        assertThatThrownBy(() -> service.generateCertificates(importResult, template))
                .isInstanceOf(IOException.class)
                .hasMessage("conversion failed");

        verify(certificatePersistenceService).persistIssuedCertificate(answer.getStudent(), false);
        verify(certificatePersistenceService, never()).persistIssuedCertificate(answer.getStudent(), true);
    }

    private AnswerSheetResult approvedAnswer() {
        Student student = new Student();
        student.setName("Ada Lovelace");
        student.setEmail("ada@example.com");

        Take take = new Take();
        take.setApproved(true);

        AnswerSheetResult answer = new AnswerSheetResult();
        answer.setStudent(student);
        answer.setTake(take);
        return answer;
    }
}
