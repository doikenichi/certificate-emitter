package com.br.shizen.certificateemitter.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "certificate")
@Getter
@Setter
@NoArgsConstructor
public class Certificate {
    @Id
    @SequenceGenerator(name = "Certificate_Generator", sequenceName = "certificate_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Certificate_Generator")
    @Column(name = "certificate_id", nullable = false)
    private Long id;

    @JsonIgnoreProperties({"name", "hibernateLazyInitializer"})
    @JoinColumn(name = "student_id", referencedColumnName = "student_id", nullable = false)
    @OneToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    private Student student;

    @Column(name = "is_issued", nullable = false)
    private boolean isIssued;

    public Certificate(Student student, boolean issued) {
        this.student = student;
        this.isIssued = issued;
    }
}
