package com.br.shizen.certificateemitter.dto;

public record ImportError(
        String field,
        String message
) {
}
