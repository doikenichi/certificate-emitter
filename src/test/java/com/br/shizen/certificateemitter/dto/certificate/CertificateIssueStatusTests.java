package com.br.shizen.certificateemitter.dto.certificate;

import com.br.shizen.certificateemitter.entity.Student;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class CertificateIssueStatusTests {

    @Test
    void newStatusDefaultsToNoStudentNoRenderedFilesAndNotIssued() {
        CertificateIssueStatus status = new CertificateIssueStatus();

        assertThat(status.getStudent()).isNull();
        assertThat(status.getRenderedCertificate()).isNull();
        assertThat(status.getRenderedPdfCertificate()).isNull();
        assertThat(status.isIssued()).isFalse();
    }

    @Test
    void storesStudentAndRenderedCertificateFiles() {
        CertificateIssueStatus status = new CertificateIssueStatus();
        Student student = new Student();
        File renderedCertificate = new File("student.docx");
        File renderedPdfCertificate = new File("student.pdf");

        status.setStudent(student);
        status.setRenderedCertificate(renderedCertificate);
        status.setRenderedPdfCertificate(renderedPdfCertificate);

        assertThat(status.getStudent()).isSameAs(student);
        assertThat(status.getRenderedCertificate()).isEqualTo(renderedCertificate);
        assertThat(status.getRenderedPdfCertificate()).isEqualTo(renderedPdfCertificate);
    }

    @Test
    void storesIssuedStateTransitions() {
        CertificateIssueStatus status = new CertificateIssueStatus();

        status.setIssued(true);

        assertThat(status.isIssued()).isTrue();

        status.setIssued(false);

        assertThat(status.isIssued()).isFalse();
    }
}
