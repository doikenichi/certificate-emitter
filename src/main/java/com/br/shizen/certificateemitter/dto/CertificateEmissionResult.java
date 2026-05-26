package com.br.shizen.certificateemitter.dto;

import java.util.List;

public record CertificateEmissionResult(
        int rowsRead,
        int rowsImported,
        int rowsSkipped,
        int studentsCreated,
        int takesCreated,
        int responsesCreated,
        int responsesUpdated,
        int certificatesGenerated,
        List<ImportError> errors
) {
    public static CertificateEmissionResult failed(
            int rowsRead,
            List<ImportError> errors
    ) {
        return new CertificateEmissionResult(
                rowsRead,
                0,
                rowsRead,
                0,
                0,
                0,
                0,
                0,
                errors
        );
    }
}
