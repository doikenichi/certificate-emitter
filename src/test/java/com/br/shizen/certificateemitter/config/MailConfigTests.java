package com.br.shizen.certificateemitter.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static org.assertj.core.api.Assertions.assertThat;

class MailConfigTests {

    @Test
    void javaMailSenderAppliesConfiguredSmtpTlsProperties() {
        MailConfig config = new MailConfig();

        JavaMailSender mailSender = config.javaMailSender(
                "smtp.example.com",
                587,
                "sender@example.com",
                "secret",
                "true",
                "true",
                "true",
                "smtp.example.com",
                "5000",
                "6000",
                "7000",
                "true"
        );

        JavaMailSenderImpl sender = (JavaMailSenderImpl) mailSender;

        assertThat(sender.getHost()).isEqualTo("smtp.example.com");
        assertThat(sender.getPort()).isEqualTo(587);
        assertThat(sender.getUsername()).isEqualTo("sender@example.com");
        assertThat(sender.getPassword()).isEqualTo("secret");
        assertThat(sender.getJavaMailProperties())
                .containsEntry("mail.smtp.auth", "true")
                .containsEntry("mail.smtp.starttls.enable", "true")
                .containsEntry("mail.smtp.starttls.required", "true")
                .containsEntry("mail.smtp.connectiontimeout", "5000")
                .containsEntry("mail.smtp.timeout", "6000")
                .containsEntry("mail.smtp.writetimeout", "7000")
                .containsEntry("mail.smtp.ssl.trust", "smtp.example.com")
                .containsEntry("mail.debug", "true");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t"})
    void javaMailSenderOmitsSmtpSslTrustWhenConfiguredValueIsBlank(String smtpSslTrust) {
        MailConfig config = new MailConfig();

        JavaMailSender mailSender = config.javaMailSender(
                "smtp.example.com",
                25,
                "sender@example.com",
                "secret",
                "false",
                "false",
                "false",
                smtpSslTrust,
                "1",
                "1",
                "1",
                "false"
        );

        JavaMailSenderImpl sender = (JavaMailSenderImpl) mailSender;

        assertThat(sender.getJavaMailProperties())
                .doesNotContainKey("mail.smtp.ssl.trust")
                .containsEntry("mail.smtp.auth", "false")
                .containsEntry("mail.smtp.starttls.enable", "false")
                .containsEntry("mail.smtp.starttls.required", "false")
                .containsEntry("mail.debug", "false");
    }
}
