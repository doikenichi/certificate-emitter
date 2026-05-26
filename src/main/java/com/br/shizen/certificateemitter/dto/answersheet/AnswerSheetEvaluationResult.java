package com.br.shizen.certificateemitter.dto.answersheet;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AnswerSheetEvaluationResult {
    List<AnswerSheetEvaluation> evaluations;
}

