package com.br.shizen.certificateemitter.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CertificateEmissionResultTests {

    @Test
    void storesEmissionCountersAndErrors() {
        List<ImportError> errors = List.of(new ImportError("email", "Email is empty"));

        CertificateEmissionResult result = new CertificateEmissionResult(
                10,
                8,
                2,
                3,
                4,
                5,
                6,
                7,
                errors
        );

        assertThat(result.rowsRead()).isEqualTo(10);
        assertThat(result.rowsImported()).isEqualTo(8);
        assertThat(result.rowsSkipped()).isEqualTo(2);
        assertThat(result.studentsCreated()).isEqualTo(3);
        assertThat(result.takesCreated()).isEqualTo(4);
        assertThat(result.responsesCreated()).isEqualTo(5);
        assertThat(result.responsesUpdated()).isEqualTo(6);
        assertThat(result.certificatesGenerated()).isEqualTo(7);
        assertThat(result.errors()).isSameAs(errors);
    }

    @Test
    void failedResultWithNoRowsHasZeroCountersAndNoSkippedRows() {
        List<ImportError> errors = List.of();

        CertificateEmissionResult result = CertificateEmissionResult.failed(0, errors);

        assertThat(result.rowsRead()).isZero();
        assertThat(result.rowsImported()).isZero();
        assertThat(result.rowsSkipped()).isZero();
        assertThat(result.studentsCreated()).isZero();
        assertThat(result.takesCreated()).isZero();
        assertThat(result.responsesCreated()).isZero();
        assertThat(result.responsesUpdated()).isZero();
        assertThat(result.certificatesGenerated()).isZero();
        assertThat(result.errors()).isSameAs(errors);
    }

    @Test
    void failedResultSkipsEveryReadRowAndKeepsErrors() {
        ImportError error = new ImportError("Nome", "Student name is empty");
        List<ImportError> errors = List.of(error);

        CertificateEmissionResult result = CertificateEmissionResult.failed(3, errors);

        assertThat(result.rowsRead()).isEqualTo(3);
        assertThat(result.rowsImported()).isZero();
        assertThat(result.rowsSkipped()).isEqualTo(3);
        assertThat(result.studentsCreated()).isZero();
        assertThat(result.takesCreated()).isZero();
        assertThat(result.responsesCreated()).isZero();
        assertThat(result.responsesUpdated()).isZero();
        assertThat(result.certificatesGenerated()).isZero();
        assertThat(result.errors()).containsExactly(error);
    }
}
