package com.br.shizen.certificateemitter.services;

import com.br.shizen.certificateemitter.dto.EmailSendResult;
import com.br.shizen.certificateemitter.dto.EmailTemplate;
import com.br.shizen.certificateemitter.dto.certificate.CertificateIssueStatus;
import com.br.shizen.certificateemitter.entity.Student;
import jakarta.mail.BodyPart;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmailSenderServiceTests {

    @TempDir
    private Path tempDir;

    @Test
    void sendCertificatesSendsEmailForSingleIssuedCertificate() throws Exception {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        EmailSenderService service = new EmailSenderService(mailSender);
        ReflectionTestUtils.setField(service, "fromMe", "sender@example.com");

        Student student = new Student();
        student.setName("Ada Lovelace");
        student.setEmail("ada@example.com");

        File certificate = Files.writeString(tempDir.resolve("certificate.pdf"), "pdf content").toFile();
        CertificateIssueStatus issueStatus = new CertificateIssueStatus();
        issueStatus.setIssued(true);
        issueStatus.setStudent(student);
        issueStatus.setRenderedCertificate(certificate);

        EmailTemplate template = new EmailTemplate(
                "completion-email",
                "completion-email.html",
                "<p>Hello {{nome}}</p>"
        );

        EmailSendResult result = service.sendCertificates(List.of(issueStatus), template);

        assertThat(result.emailsAttempted()).isEqualTo(1);
        assertThat(result.emailsSent()).isEqualTo(1);
        assertThat(result.emailsSkipped()).isZero();
        assertThat(result.errors()).isEmpty();

        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        MimeMessage sentMessage = messageCaptor.getValue();
        assertThat(sentMessage.getFrom()[0].toString()).isEqualTo("sender@example.com");
        assertThat(sentMessage.getAllRecipients()[0].toString()).isEqualTo("ada@example.com");
        assertThat(sentMessage.getSubject()).isEqualTo("Your certificate is ready!");

        assertThat(containsText(sentMessage, "Hello Ada Lovelace")).isTrue();
        assertThat(containsAttachment(sentMessage, "Ada Lovelace.pdf")).isTrue();
    }

    private boolean containsText(Part part, String expectedText) throws Exception {
        Object content = part.getContent();
        if (content instanceof String text) {
            return text.contains(expectedText);
        }
        if (content instanceof Multipart multipart) {
            for (int i = 0; i < multipart.getCount(); i++) {
                if (containsText(multipart.getBodyPart(i), expectedText)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean containsAttachment(Part part, String expectedFileName) throws Exception {
        if (expectedFileName.equals(part.getFileName())) {
            return true;
        }

        Object content = part.getContent();
        if (content instanceof Multipart multipart) {
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                if (containsAttachment(bodyPart, expectedFileName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
