package com.br.shizen.certificateemitter.services.answersheet;

import com.br.shizen.certificateemitter.dto.ImportError;
import com.br.shizen.certificateemitter.dto.answersheet.AnswerSheetRow;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AnswerSheetValidator {
    public List<ImportError> validate(List<AnswerSheetRow> rows) {
        List<ImportError> errors = new ArrayList<>();
        for (var row : rows) {
            if (isBlank(row.getStudentName())) {
                errors.add(new ImportError("Nome", "Student name is empty"));
            }
            if (isBlank(row.getStudentEmail())) {
                errors.add(new ImportError("Email", "Student email is empty"));
            }
            if (isBlank(row.getAnswer1())
                    && isBlank(row.getAnswer2())
                    && isBlank(row.getAnswer3())
                    && isBlank(row.getAnswer4())
                    && isBlank(row.getAnswer5())
                    && isBlank(row.getAnswer6())
                    && isBlank(row.getAnswer7())
                    && isBlank(row.getAnswer8())
                    && isBlank(row.getAnswer9())
                    && isBlank(row.getAnswer10())) {
                errors.add(new ImportError("Answer", "Answer is empty"));
            }
        }
        return errors;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
