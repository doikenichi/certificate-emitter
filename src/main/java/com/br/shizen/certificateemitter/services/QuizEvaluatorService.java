package com.br.shizen.certificateemitter.services;

import com.br.shizen.certificateemitter.repository.ResponseRepository;
import com.br.shizen.certificateemitter.repository.StudentRepository;
import com.br.shizen.certificateemitter.repository.TakeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuizEvaluatorService {

    private final StudentRepository studentRepository;
    private final TakeRepository takeRepository;
    private final ResponseRepository responseRepository;

    public void evaluateQuiz() {
    }
}
