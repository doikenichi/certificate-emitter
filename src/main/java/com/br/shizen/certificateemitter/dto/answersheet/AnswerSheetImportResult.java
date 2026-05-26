package com.br.shizen.certificateemitter.dto.answersheet;

import com.br.shizen.certificateemitter.entity.Question;
import com.br.shizen.certificateemitter.entity.Quiz;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class AnswerSheetImportResult {
    private Quiz quiz;
    private List<Question> questions;
    private List<AnswerSheetResult> answers;
}
