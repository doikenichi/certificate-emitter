package com.br.shizen.certificateemitter.dto;

public record EmailTemplate(
        String name,
        String localPath,
        String content
) {
}
