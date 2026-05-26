package com.br.shizen.certificateemitter.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ImportErrorTests {

    @Test
    void storesFieldAndMessage() {
        ImportError error = new ImportError("Email", "Student email is empty");

        assertThat(error.field()).isEqualTo("Email");
        assertThat(error.message()).isEqualTo("Student email is empty");
    }

    @Test
    void allowsMissingFieldAndMessageWhenCallerProvidesNulls() {
        ImportError error = new ImportError(null, null);

        assertThat(error.field()).isNull();
        assertThat(error.message()).isNull();
    }
}
