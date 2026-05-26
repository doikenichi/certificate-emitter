package com.br.shizen.certificateemitter.services.answersheet;

public interface ScoreCalculator {
    int calculate(int correctAnswers, int totalQuestions);
}
