package com.br.shizen.certificateemitter.services.answersheet;

import com.br.shizen.certificateemitter.dto.ImportError;
import com.br.shizen.certificateemitter.dto.answersheet.AnswerSheetRow;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AnswerSheetValidatorTests {

    private final AnswerSheetValidator validator = new AnswerSheetValidator();

    @Test
    void acceptsRowWithStudentIdentityAndAtLeastOneAnswer() {
        List<ImportError> errors = validator.validate(List.of(row("Ada Lovelace", "ada@example.com", "A", "", "", "", "", "", "", "", "", "")));

        assertThat(errors).isEmpty();
    }

    @Test
    void reportsNameEmailAndAnswerErrorsForEmptyRequiredFields() {
        List<ImportError> errors = validator.validate(List.of(row("", "", "", "", "", "", "", "", "", "", "", "")));

        assertThat(errors)
                .containsExactly(
                        new ImportError("Nome", "Student name is empty"),
                        new ImportError("Email", "Student email is empty"),
                        new ImportError("Answer", "Answer is empty")
                );
    }

    @Test
    void treatsNullAndBlankCellsAsEmpty() {
        List<ImportError> errors = validator.validate(List.of(row(null, "   ", null, " ", "", null, "", " ", null, "", " ", null)));

        assertThat(errors)
                .containsExactly(
                        new ImportError("Nome", "Student name is empty"),
                        new ImportError("Email", "Student email is empty"),
                        new ImportError("Answer", "Answer is empty")
                );
    }

    @Test
    void doesNotReportAnswerErrorWhenAnyAnswerIsPresent() {
        List<ImportError> errors = validator.validate(List.of(row("Ada Lovelace", "ada@example.com", "", "", "", "", "", "", "", "", "", "D")));

        assertThat(errors).isEmpty();
    }

    private AnswerSheetRow row(
            String name,
            String email,
            String answer1,
            String answer2,
            String answer3,
            String answer4,
            String answer5,
            String answer6,
            String answer7,
            String answer8,
            String answer9,
            String answer10
    ) {
        return new AnswerSheetRow(name, email, answer1, answer2, answer3, answer4, answer5, answer6, answer7, answer8, answer9, answer10);
    }
}
