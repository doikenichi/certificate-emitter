package com.br.shizen.certificateemitter.services.answersheet;

import com.br.shizen.certificateemitter.dto.answersheet.AnswerSheetEvaluation;
import com.br.shizen.certificateemitter.dto.answersheet.AnswerSheetImportResult;
import com.br.shizen.certificateemitter.dto.answersheet.AnswerSheetResult;
import com.br.shizen.certificateemitter.dto.answersheet.AnswerSheetRow;
import com.br.shizen.certificateemitter.entity.*;
import com.br.shizen.certificateemitter.repository.QuestionRepository;
import com.br.shizen.certificateemitter.repository.QuizRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerSheetEvaluator {

    @Getter
    @Setter
    @Value("${evaluation.minApproval}")
    private int minApprovalScore;

    private final ScoreCalculator scoreCalculator;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;

    public AnswerSheetImportResult evaluate(@NonNull List<AnswerSheetRow> rows) {
        AnswerSheetImportResult result = new AnswerSheetImportResult();
        List<Quiz> quizes = this.quizRepository.findAll();
        if (quizes.size() != 1) {
            throw new IllegalStateException("There must be only one quiz");
        }
        Date now = new Date(System.currentTimeMillis());
        Quiz quiz = quizes.getFirst();
        List<Question> questions = this.questionRepository.findAllByQuiz(quiz);
        if (questions.isEmpty()) {
            throw new IllegalArgumentException("Cannot evaluate answer sheet with zero questions");
        }
        int totalQuestions = questions.size();


        List<AnswerSheetResult> answers = new ArrayList<>();

        result.setQuiz(quiz);
        result.setQuestions(questions);
        rows.forEach(row -> {
            AnswerSheetResult answerResult = new AnswerSheetResult();
            // set student
            Student student = new Student();
            student.setEmail(row.getStudentEmail());
            student.setName(row.getStudentName());

            // set responses
            answerResult.getResponses().clear();
            answerResult.getResponses().add(this.setResponse(questions.getFirst(), row.getAnswer1()));
            answerResult.getResponses().add(this.setResponse(questions.get(1), row.getAnswer2()));
            answerResult.getResponses().add(this.setResponse(questions.get(2), row.getAnswer3()));
            answerResult.getResponses().add(this.setResponse(questions.get(3), row.getAnswer4()));
            answerResult.getResponses().add(this.setResponse(questions.get(4), row.getAnswer5()));
            answerResult.getResponses().add(this.setResponse(questions.get(5), row.getAnswer6()));
            answerResult.getResponses().add(this.setResponse(questions.get(6), row.getAnswer7()));
            answerResult.getResponses().add(this.setResponse(questions.get(7), row.getAnswer8()));
            answerResult.getResponses().add(this.setResponse(questions.get(8), row.getAnswer9()));
            answerResult.getResponses().add(this.setResponse(questions.get(9), row.getAnswer10()));

            // evaluate the responses
            AnswerSheetEvaluation evaluation = evaluateAnswer(answerResult, totalQuestions);

            // set take
            Take take = new Take();
            take.setQuiz(quiz);
            take.setDateTaken(now);
            take.setScore(evaluation.getScore());
            take.setApproved(evaluation.isApproved());

            answerResult.setStudent(student);
            answerResult.setTake(take);
            answerResult.getEvaluations().clear();
            answerResult.getEvaluations().add(evaluation);

            answers.add(answerResult);
        });
        result.setAnswers(answers);
        return result;
    }

    private @NonNull Response setResponse(Question question, String answer) {
        Response response = new Response();
        response.setQuestion(question);
        response.setResponse(answer);
        response.setCorrect(question.getCorrectAnswer().equalsIgnoreCase(answer));
        return response;
    }

    private @NonNull AnswerSheetEvaluation evaluateAnswer(@NonNull AnswerSheetResult answer, int totalQuestions) {
        int correctAnswers = Math.toIntExact(answer.getResponses()
                .stream()
                .filter(Response::isCorrect)
                .count());

        AnswerSheetEvaluation evaluation = new AnswerSheetEvaluation();
        evaluation.setCorrectAnswers(correctAnswers);
        int score = scoreCalculator.calculate(correctAnswers, totalQuestions);
        evaluation.setScore(score);
        boolean isApproved = score >= this.minApprovalScore;
        evaluation.setApproved(isApproved);

        return evaluation;
    }
}