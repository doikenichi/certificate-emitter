package com.br.shizen.certificateemitter.services.certificate;

import com.br.shizen.certificateemitter.entity.Certificate;
import com.br.shizen.certificateemitter.entity.Student;
import com.br.shizen.certificateemitter.repository.CertificateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CertificatePersistenceService {

    private final CertificateRepository certificateRepository;

    @Transactional
    public void persistIssuedCertificate(Student student, boolean issued) {
        certificateRepository.findByStudentId(student.getId())
                .map(certificate -> {
                    if (!certificate.isIssued()) {
                        certificate.setIssued(issued);
                        certificateRepository.save(certificate);
                    }
                    return false;
                })
                .orElseGet(() -> {
                    certificateRepository.save(new Certificate(student, issued));
                    return true;
                });
    }
}
