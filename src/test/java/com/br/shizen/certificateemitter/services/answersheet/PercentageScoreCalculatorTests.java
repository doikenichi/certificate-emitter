package com.br.shizen.certificateemitter.services.answersheet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PercentageScoreCalculatorTests {

    private final PercentageScoreCalculator calculator = new PercentageScoreCalculator();

    @ParameterizedTest
    @CsvSource({
            "0, 10, 0",
            "1, 10, 10",
            "9, 10, 90",
            "10, 10, 100",
            "1, 3, 33"
    })
    void calculatesIntegerPercentageAtScoreBoundaries(int correctAnswers, int totalQuestions, int expectedScore) {
        assertThat(calculator.calculate(correctAnswers, totalQuestions)).isEqualTo(expectedScore);
    }

    @Test
    void rejectsZeroTotalQuestions() {
        assertThatThrownBy(() -> calculator.calculate(0, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Total questions cannot be zero");
    }

    @ParameterizedTest
    @CsvSource({
            "-1, 10",
            "11, 10"
    })
    void rejectsCorrectAnswerCountsOutsideQuestionBounds(int correctAnswers, int totalQuestions) {
        assertThatThrownBy(() -> calculator.calculate(correctAnswers, totalQuestions))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Correct answers must be between 0 and total questions");
    }
}
