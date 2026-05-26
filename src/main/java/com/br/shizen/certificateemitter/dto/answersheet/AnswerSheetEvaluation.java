package com.br.shizen.certificateemitter.dto.answersheet;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerSheetEvaluation {
    private int correctAnswers;
    private int score;
    private boolean approved;
}
