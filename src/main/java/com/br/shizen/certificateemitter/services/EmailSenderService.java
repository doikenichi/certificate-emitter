package com.br.shizen.certificateemitter.services;


import com.br.shizen.certificateemitter.dto.EmailSendResult;
import com.br.shizen.certificateemitter.dto.EmailTemplate;
import com.br.shizen.certificateemitter.dto.ImportError;
import com.br.shizen.certificateemitter.dto.certificate.CertificateIssueStatus;
import com.br.shizen.certificateemitter.entity.Student;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderService {
    private static final String CERTIFICATE_EMAIL_SUBJECT = "Your certificate is ready!";

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromMe;

    public EmailSendResult sendCertificates(
            List<CertificateIssueStatus> certificateIssueStatusList,
            EmailTemplate emailTemplate
    ) {
        int emailsAttempted = 0;
        int emailsSent = 0;
        int emailsSkipped = 0;
        List<ImportError> errors = new ArrayList<>();

        for (CertificateIssueStatus certificateIssueStatus : certificateIssueStatusList) {
            if (!certificateIssueStatus.isIssued()) {
                emailsSkipped++;
                continue;
            }

            Student student = certificateIssueStatus.getStudent();
            File renderedCertificate = certificateIssueStatus.getRenderedPdfCertificate() == null
                    ? certificateIssueStatus.getRenderedCertificate()
                    : certificateIssueStatus.getRenderedPdfCertificate();
            if (student == null || student.getEmail() == null || student.getEmail().isBlank() || renderedCertificate == null) {
                emailsSkipped++;
                errors.add(new ImportError("email", "Certificate email skipped due to missing recipient or attachment"));
                continue;
            }

            emailsAttempted++;
            try {
                String htmlBody = emailTemplate.content().replace("{{nome}}", student.getName());
                String attachmentName = student.getName() + ".pdf";
                sendHtmlEmailWithAttachment(
                        "me",
                        student.getEmail(),
                        CERTIFICATE_EMAIL_SUBJECT,
                        htmlBody,
                        renderedCertificate,
                        attachmentName
                );
                emailsSent++;
            } catch (MessagingException e) {
                log.error("Error sending email to student {}: {}", student.getEmail(), e.getMessage());
                errors.add(new ImportError(student.getEmail(), e.getMessage()));
            }
        }

        return new EmailSendResult(emailsAttempted, emailsSent, emailsSkipped, errors);
    }

    public void sendHtmlEmailWithAttachment(String from, String to, String subject, String htmlBody, File attachment, String attachmentName) throws MessagingException {
        MimeMessage msg = this.mailSender.createMimeMessage();

        // true = multipart message
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);

        if (from == null || from.isEmpty() || "me".equalsIgnoreCase(from)) {
            helper.setFrom(this.fromMe);
        } else {
            helper.setFrom(from);
        }
        helper.setTo(to);
        helper.setSubject(subject);

        // true = text/html
        helper.setText(htmlBody, true);

        // hard coded a file path
        //FileSystemResource file = new FileSystemResource(new File("path/android.png"));

        helper.addAttachment(attachmentName, new FileSystemResource(attachment));

        this.mailSender.send(msg);
    }
}
