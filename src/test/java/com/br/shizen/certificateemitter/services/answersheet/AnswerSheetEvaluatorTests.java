package com.br.shizen.certificateemitter.services.answersheet;

import com.br.shizen.certificateemitter.dto.answersheet.AnswerSheetImportResult;
import com.br.shizen.certificateemitter.dto.answersheet.AnswerSheetRow;
import com.br.shizen.certificateemitter.entity.Question;
import com.br.shizen.certificateemitter.entity.Quiz;
import com.br.shizen.certificateemitter.repository.QuestionRepository;
import com.br.shizen.certificateemitter.repository.QuizRepository;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AnswerSheetEvaluatorTests {

    private final ScoreCalculator scoreCalculator = new PercentageScoreCalculator();
    private final QuizRepository quizRepository = mock(QuizRepository.class);
    private final QuestionRepository questionRepository = mock(QuestionRepository.class);
    private final AnswerSheetEvaluator evaluator = new AnswerSheetEvaluator(scoreCalculator, quizRepository, questionRepository);

    @Test
    void approvesScoreAtMinimumThresholdAndMatchesAnswersCaseInsensitively() {
        Quiz quiz = new Quiz();
        List<Question> questions = questionsWithCorrectAnswer("A");
        when(quizRepository.findAll()).thenReturn(List.of(quiz));
        when(questionRepository.findAllByQuiz(quiz)).thenReturn(questions);
        evaluator.setMinApprovalScore(70);

        AnswerSheetRow row = new AnswerSheetRow(
                "Ada Lovelace",
                "ada@example.com",
                "a", "A", "A", "A", "A", "A", "A", "B", "B", "B"
        );

        AnswerSheetImportResult result = evaluator.evaluate(List.of(row));

        assertThat(result.getQuiz()).isSameAs(quiz);
        assertThat(result.getQuestions()).isSameAs(questions);
        assertThat(result.getAnswers()).hasSize(1);
        assertThat(result.getAnswers().getFirst().getStudent().getName()).isEqualTo("Ada Lovelace");
        assertThat(result.getAnswers().getFirst().getStudent().getEmail()).isEqualTo("ada@example.com");
        assertThat(result.getAnswers().getFirst().getResponses()).hasSize(10);
        assertThat(result.getAnswers().getFirst().getResponses()).filteredOn(response -> response.isCorrect()).hasSize(7);
        assertThat(result.getAnswers().getFirst().getEvaluations().getFirst().getCorrectAnswers()).isEqualTo(7);
        assertThat(result.getAnswers().getFirst().getEvaluations().getFirst().getScore()).isEqualTo(70);
        assertThat(result.getAnswers().getFirst().getEvaluations().getFirst().isApproved()).isTrue();
        assertThat(result.getAnswers().getFirst().getTake().getScore()).isEqualTo(70);
        assertThat(result.getAnswers().getFirst().getTake().isApproved()).isTrue();
        assertThat(result.getAnswers().getFirst().getTake().getQuiz()).isSameAs(quiz);
        assertThat(result.getAnswers().getFirst().getTake().getDateTaken()).isNotNull();
    }

    @Test
    void rejectsScoreBelowMinimumThreshold() {
        Quiz quiz = new Quiz();
        when(quizRepository.findAll()).thenReturn(List.of(quiz));
        when(questionRepository.findAllByQuiz(quiz)).thenReturn(questionsWithCorrectAnswer("A"));
        evaluator.setMinApprovalScore(80);

        AnswerSheetRow row = new AnswerSheetRow(
                "Ada Lovelace",
                "ada@example.com",
                "A", "A", "A", "A", "A", "A", "A", "B", "B", "B"
        );

        AnswerSheetImportResult result = evaluator.evaluate(List.of(row));

        assertThat(result.getAnswers().getFirst().getEvaluations().getFirst().getScore()).isEqualTo(70);
        assertThat(result.getAnswers().getFirst().getEvaluations().getFirst().isApproved()).isFalse();
        assertThat(result.getAnswers().getFirst().getTake().isApproved()).isFalse();
    }

    @Test
    void rejectsWhenThereIsNoSingleQuiz() {
        when(quizRepository.findAll()).thenReturn(List.of(new Quiz(), new Quiz()));

        assertThatThrownBy(() -> evaluator.evaluate(List.of(validRow())))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("There must be only one quiz");
    }

    @Test
    void rejectsQuizWithZeroQuestions() {
        Quiz quiz = new Quiz();
        when(quizRepository.findAll()).thenReturn(List.of(quiz));
        when(questionRepository.findAllByQuiz(quiz)).thenReturn(List.of());

        assertThatThrownBy(() -> evaluator.evaluate(List.of(validRow())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot evaluate answer sheet with zero questions");
    }

    private AnswerSheetRow validRow() {
        return new AnswerSheetRow("Ada Lovelace", "ada@example.com", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A");
    }

    private List<Question> questionsWithCorrectAnswer(String correctAnswer) {
        return List.of(
                question(correctAnswer),
                question(correctAnswer),
                question(correctAnswer),
                question(correctAnswer),
                question(correctAnswer),
                question(correctAnswer),
                question(correctAnswer),
                question(correctAnswer),
                question(correctAnswer),
                question(correctAnswer)
        );
    }

    private Question question(String correctAnswer) {
        Question question = new Question();
        ReflectionTestUtils.setField(question, "correctAnswer", correctAnswer);
        return question;
    }
}
