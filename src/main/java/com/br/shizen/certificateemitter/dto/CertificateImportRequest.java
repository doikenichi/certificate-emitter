package com.br.shizen.certificateemitter.dto;

import jakarta.validation.constraints.NotBlank;

public record CertificateImportRequest(
        @NotBlank String formAnswersName,
        @NotBlank String certificateTemplate,
        @NotBlank String emailTemplateName
) {
}
