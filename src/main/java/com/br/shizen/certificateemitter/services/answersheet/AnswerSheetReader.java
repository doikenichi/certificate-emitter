package com.br.shizen.certificateemitter.services.answersheet;

import com.br.shizen.certificateemitter.dto.answersheet.AnswerSheetRow;

import java.io.File;
import java.util.List;

public interface AnswerSheetReader {
    public List<AnswerSheetRow> read(File file);
}
