package com.br.shizen.certificateemitter.dto.certificate;

import com.br.shizen.certificateemitter.entity.Student;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
public class CertificateIssueStatus {

    private Student student;
    private File renderedCertificate;
    private File renderedPdfCertificate;
    private boolean issued;

}
