package com.br.shizen.certificateemitter.dto.answersheet;

import com.br.shizen.certificateemitter.entity.Response;
import com.br.shizen.certificateemitter.entity.Student;
import com.br.shizen.certificateemitter.entity.Take;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AnswerSheetResult {
    private Student student;
    private Take take;
    private List<Response> responses;
    private List<AnswerSheetEvaluation> evaluations;

    public AnswerSheetResult() {
        this.evaluations = new ArrayList<>();
        this.responses = new ArrayList<>();
    }
}
