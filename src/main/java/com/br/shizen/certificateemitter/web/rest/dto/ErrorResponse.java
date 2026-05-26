package com.br.shizen.certificateemitter.web.rest.dto;

public record ErrorResponse(
        String status,
        String message
) {
    public static ErrorResponse error(String message) {
        return new ErrorResponse("error", message);
    }
}
