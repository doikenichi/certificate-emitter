package com.br.shizen.certificateemitter.services.domain;

import com.br.shizen.certificateemitter.dto.certificate.CertificateIssueStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class IssueControlFile {
    public String email;
    public Date issuedDate;
    public CertificateIssueStatus status;
}
