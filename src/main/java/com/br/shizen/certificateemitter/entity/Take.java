package com.br.shizen.certificateemitter.entity;

import com.br.shizen.certificateemitter.entity.id.TakeId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Entity
@Table(name = "take")
public class Take {

    @EmbeddedId
    private TakeId id = new TakeId();

    @Getter
    @Setter
    @JsonIgnoreProperties({"name", "hibernateLazyInitializer"})
    @MapsId("studentId")
    @JoinColumn(name = "student_id", referencedColumnName = "student_id", nullable = false)
    @ManyToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    private Student student;

    @Getter
    @Setter
    @JsonIgnoreProperties({"name", "description", "hibernateLazyInitializer"})
    @MapsId("quizId")
    @JoinColumn(name = "quiz_id", referencedColumnName = "quiz_id", nullable = false)
    @ManyToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    private Quiz quiz;

    @Getter
    @Setter
    @Column(name = "score", nullable = true)
    private Integer score;

    @Getter
    @Setter
    @Column(name = "date_taken", nullable = false)
    private Date dateTaken;

    @Getter
    @Setter
    @Column(name = "is_approved", nullable = false)
    private boolean isApproved;
}
