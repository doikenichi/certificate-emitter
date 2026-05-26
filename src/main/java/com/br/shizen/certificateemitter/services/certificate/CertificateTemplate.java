package com.br.shizen.certificateemitter.services.certificate;

public record CertificateTemplate(
        String name,
        String localPath,
        String googleFileId
) {
}
