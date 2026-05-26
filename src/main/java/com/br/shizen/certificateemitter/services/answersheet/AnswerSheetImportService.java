package com.br.shizen.certificateemitter.services.answersheet;

import com.br.shizen.certificateemitter.dto.ImportError;
import com.br.shizen.certificateemitter.dto.answersheet.AnswerSheetImportResult;
import com.br.shizen.certificateemitter.dto.answersheet.AnswerSheetRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerSheetImportService {
    private final AnswerSheetProvider answerSheetProvider;
    private final AnswerSheetReader answerSheetReader;
    private final AnswerSheetValidator answerSheetValidator;
    private final AnswerSheetEvaluator answerSheetEvaluator;
    private final AnswerSheetPersistenceService answerSheetPersistenceService;

    public AnswerSheetImportResult importAnswerSheet(String answerSheetName) throws IOException {
        File answerSheet = answerSheetProvider.download(answerSheetName);
        List<AnswerSheetRow> rows;
        try {
            rows = answerSheetReader.read(answerSheet);
        } finally {
            Files.deleteIfExists(answerSheet.toPath());
        }

        List<ImportError> rowErrors = answerSheetValidator.validate(rows);
        if (!rowErrors.isEmpty()) {
            throw new AnswerSheetValidationException(rows.size(), rowErrors);
        }

        AnswerSheetImportResult answerImport = answerSheetEvaluator.evaluate(rows);
        return answerSheetPersistenceService.persist(answerImport);
    }
}
