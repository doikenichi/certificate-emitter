package com.br.shizen.certificateemitter.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TakeId implements Serializable {
    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "quiz_id", nullable = false)
    private Long quizId;
}
