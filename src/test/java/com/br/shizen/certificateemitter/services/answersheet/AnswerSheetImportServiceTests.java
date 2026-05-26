package com.br.shizen.certificateemitter.services.answersheet;

import com.br.shizen.certificateemitter.dto.ImportError;
import com.br.shizen.certificateemitter.dto.answersheet.AnswerSheetImportResult;
import com.br.shizen.certificateemitter.dto.answersheet.AnswerSheetRow;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AnswerSheetImportServiceTests {

    private final AnswerSheetProvider answerSheetProvider = mock(AnswerSheetProvider.class);
    private final AnswerSheetReader answerSheetReader = mock(AnswerSheetReader.class);
    private final AnswerSheetValidator answerSheetValidator = mock(AnswerSheetValidator.class);
    private final AnswerSheetEvaluator answerSheetEvaluator = mock(AnswerSheetEvaluator.class);
    private final AnswerSheetPersistenceService answerSheetPersistenceService = mock(AnswerSheetPersistenceService.class);
    private final AnswerSheetImportService service = new AnswerSheetImportService(
            answerSheetProvider,
            answerSheetReader,
            answerSheetValidator,
            answerSheetEvaluator,
            answerSheetPersistenceService
    );

    @Test
    void importsValidAnswerSheetAndPersistsEvaluatedResult() throws Exception {
        File file = Files.createTempFile("answers", ".xlsx").toFile();
        List<AnswerSheetRow> rows = List.of(validRow());
        AnswerSheetImportResult evaluatedResult = new AnswerSheetImportResult();
        AnswerSheetImportResult persistedResult = new AnswerSheetImportResult();
        when(answerSheetProvider.download("answers")).thenReturn(file);
        when(answerSheetReader.read(file)).thenReturn(rows);
        when(answerSheetValidator.validate(rows)).thenReturn(List.of());
        when(answerSheetEvaluator.evaluate(rows)).thenReturn(evaluatedResult);
        when(answerSheetPersistenceService.persist(evaluatedResult)).thenReturn(persistedResult);

        AnswerSheetImportResult result = service.importAnswerSheet("answers");

        assertThat(result).isSameAs(persistedResult);
        assertThat(file).doesNotExist();

        var inOrder = inOrder(answerSheetProvider, answerSheetReader, answerSheetValidator, answerSheetEvaluator, answerSheetPersistenceService);
        inOrder.verify(answerSheetProvider).download("answers");
        inOrder.verify(answerSheetReader).read(file);
        inOrder.verify(answerSheetValidator).validate(rows);
        inOrder.verify(answerSheetEvaluator).evaluate(rows);
        inOrder.verify(answerSheetPersistenceService).persist(evaluatedResult);
    }

    @Test
    void deletesDownloadedFileWhenReadingFails() throws Exception {
        File file = Files.createTempFile("answers", ".xlsx").toFile();
        when(answerSheetProvider.download("answers")).thenReturn(file);
        when(answerSheetReader.read(file)).thenThrow(new RuntimeException("cannot read"));

        assertThatThrownBy(() -> service.importAnswerSheet("answers"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("cannot read");

        assertThat(file).doesNotExist();
        verify(answerSheetValidator, never()).validate(org.mockito.ArgumentMatchers.any());
        verify(answerSheetEvaluator, never()).evaluate(org.mockito.ArgumentMatchers.any());
        verify(answerSheetPersistenceService, never()).persist(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void throwsValidationExceptionBeforeEvaluatingInvalidRows() throws Exception {
        File file = Files.createTempFile("answers", ".xlsx").toFile();
        List<AnswerSheetRow> rows = List.of(validRow());
        List<ImportError> errors = List.of(new ImportError("Email", "Student email is empty"));
        when(answerSheetProvider.download("answers")).thenReturn(file);
        when(answerSheetReader.read(file)).thenReturn(rows);
        when(answerSheetValidator.validate(rows)).thenReturn(errors);

        assertThatThrownBy(() -> service.importAnswerSheet("answers"))
                .isInstanceOfSatisfying(AnswerSheetValidationException.class, exception -> {
                    assertThat(exception.getRowsRead()).isEqualTo(1);
                    assertThat(exception.getErrors()).isSameAs(errors);
                })
                .hasMessage("Answer sheet validation failed");

        assertThat(file).doesNotExist();
        verify(answerSheetEvaluator, never()).evaluate(rows);
        verify(answerSheetPersistenceService, never()).persist(org.mockito.ArgumentMatchers.any());
    }

    private AnswerSheetRow validRow() {
        return new AnswerSheetRow("Ada Lovelace", "ada@example.com", "A", "", "", "", "", "", "", "", "", "");
    }
}
