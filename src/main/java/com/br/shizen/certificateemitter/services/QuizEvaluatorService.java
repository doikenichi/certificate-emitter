package com.br.shizen.certificateemitter.services;

import com.br.shizen.certificateemitter.entity.Response;
import com.br.shizen.certificateemitter.repository.*;
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
