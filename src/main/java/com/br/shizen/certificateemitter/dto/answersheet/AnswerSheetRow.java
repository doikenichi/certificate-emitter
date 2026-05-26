package com.br.shizen.certificateemitter.dto.answersheet;

import com.poiji.annotation.ExcelCellName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnswerSheetRow {
    @ExcelCellName("Nome")
    private String studentName;

    @ExcelCellName("Email")
    private String studentEmail;

    @ExcelCellName("Q1")
    private String answer1;

    @ExcelCellName("Q2")
    private String answer2;

    @ExcelCellName("Q3")
    private String answer3;

    @ExcelCellName("Q4")
    private String answer4;

    @ExcelCellName("Q5")
    private String answer5;

    @ExcelCellName("Q6")
    private String answer6;

    @ExcelCellName("Q7")
    private String answer7;

    @ExcelCellName("Q8")
    private String answer8;

    @ExcelCellName("Q9")
    private String answer9;

    @ExcelCellName("Q10")
    private String answer10;
}
