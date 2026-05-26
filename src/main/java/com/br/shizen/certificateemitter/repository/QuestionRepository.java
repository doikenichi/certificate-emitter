package com.br.shizen.certificateemitter.repository;

import com.br.shizen.certificateemitter.entity.Question;
import com.br.shizen.certificateemitter.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findAllByQuiz(Quiz quiz);
}
