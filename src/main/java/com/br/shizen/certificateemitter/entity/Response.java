package com.br.shizen.certificateemitter.entity;

import com.br.shizen.certificateemitter.entity.id.ResponseId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "response")
public class Response {

    @EmbeddedId
    private ResponseId id = new ResponseId();

    @Getter
    @Setter
    @JsonIgnoreProperties({"score", "dateTaken", "hibernateLazyInitializer"})
    @MapsId("takeId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "student_id", referencedColumnName = "student_id", nullable = false),
            @JoinColumn(name = "quiz_id", referencedColumnName = "quiz_id", nullable = false)
    })
    private Take take;

    @Getter
    @Setter
    @JsonIgnoreProperties({"question", "option_a", "option_b", "option_c", "option_d", "correct_answer", "hibernateLazyInitializer"})
    @MapsId("questionId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", referencedColumnName = "question_id", nullable = false)
    private Question question;

    @Getter
    @Setter
    @Column(name = "response", nullable = false, length = 1)
    private String response;

    @Getter
    @Setter
    @Column(name = "is_correct", nullable = false, length = 1)
    private boolean isCorrect;
}
