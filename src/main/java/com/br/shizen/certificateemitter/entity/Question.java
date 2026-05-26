package com.br.shizen.certificateemitter.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "question")
public class Question {
    @Id
    @SequenceGenerator(name = "Question_Generator", sequenceName = "question_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Question_Generator")
    @Column(name = "question_id", nullable = false)
    private Long Id;

    @JsonIgnoreProperties({"name", "description", "hibernateLazyInitializer"})
    @JoinColumn(name = "quiz_id", referencedColumnName = "quiz_id", nullable = false)
    @ManyToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    private Quiz quiz;

    @Getter
    @Column(name = "question", nullable = false, length = 255)
    private String question;

    @Getter
    @Column(name = "option_a", nullable = false, length = 255)
    private String optionA;

    @Getter
    @Column(name = "option_b", nullable = false, length = 255)
    private String optionB;

    @Getter
    @Column(name = "option_c", nullable = false, length = 255)
    private String optionC;

    @Getter
    @Column(name = "option_d", nullable = false, length = 255)
    private String optionD;

    @Getter
    @Column(name = "correct_answer", nullable = false, length = 1)
    private String correctAnswer;
}
