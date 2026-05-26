package com.br.shizen.certificateemitter.services.answersheet;

import com.br.shizen.certificateemitter.dto.ImportError;
import lombok.Getter;

import java.util.List;

@Getter
public class AnswerSheetValidationException extends RuntimeException {
    private final int rowsRead;
    private final List<ImportError> errors;

    public AnswerSheetValidationException(int rowsRead, List<ImportError> errors) {
        super("Answer sheet validation failed");
        this.rowsRead = rowsRead;
        this.errors = errors;
    }
}
