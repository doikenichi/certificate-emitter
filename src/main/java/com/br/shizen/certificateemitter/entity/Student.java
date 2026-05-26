package com.br.shizen.certificateemitter.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "student")
public class Student {

    @Getter
    @Id
    @SequenceGenerator(name="Student_Generator", sequenceName="student_sequence", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="Student_Generator")
    @Column(name = "student_id", nullable = false)
    private Long id;

    @Getter
    @Setter
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Getter
    @Setter
    @Column(name = "email", nullable = false, length = 255)
    private String email;
}
