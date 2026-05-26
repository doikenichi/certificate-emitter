package com.br.shizen.certificateemitter.services.answersheet;

import org.springframework.stereotype.Component;

@Component
public class PercentageScoreCalculator implements ScoreCalculator {

    @Override
    public int calculate(int correctAnswers, int totalQuestions) {
        if (totalQuestions == 0) {
            throw new IllegalArgumentException("Total questions cannot be zero");
        }

        if (correctAnswers < 0 || correctAnswers > totalQuestions) {
            throw new IllegalArgumentException("Correct answers must be between 0 and total questions");
        }

        return (correctAnswers * 100) / totalQuestions;
    }
}