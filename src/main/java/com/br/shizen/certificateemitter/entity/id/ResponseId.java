package com.br.shizen.certificateemitter.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ResponseId implements Serializable {

    @Embedded
    private TakeId takeId = new TakeId();
    @Column(name = "question_id", nullable = false)
    private Long questionId;
}
