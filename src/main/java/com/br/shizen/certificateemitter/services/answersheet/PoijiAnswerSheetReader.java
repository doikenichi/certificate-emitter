package com.br.shizen.certificateemitter.services.answersheet;

import com.br.shizen.certificateemitter.dto.answersheet.AnswerSheetRow;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;

@Component
public class PoijiAnswerSheetReader implements AnswerSheetReader {
    @Override
    public List<AnswerSheetRow> read(File file) {
        try (InputStream inputStream = new FileInputStream(file)) {
            return Poiji.fromExcel(inputStream, PoijiExcelType.XLSX, AnswerSheetRow.class);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read answer sheet: " + file, e);
        }
    }
}
