package com.br.shizen.certificateemitter.services.certificate;

import com.br.shizen.certificateemitter.dto.answersheet.AnswerSheetEvaluation;
import com.br.shizen.certificateemitter.dto.answersheet.AnswerSheetResult;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class CertificateRenderer {

    private static final String MAIN_DOCUMENT_PATH = "word/document.xml";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public File render(CertificateTemplate template, AnswerSheetResult answer) throws IOException {
        String outputFileName = sanitizeFileName(answer.getStudent().getName()) + "-" + answer.getStudent().getId() + ".docx";
        Path templatePath = Path.of(template.localPath());
        Path outputPath = templatePath.getParent().resolve(outputFileName);
        Files.copy(templatePath, outputPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        replacePlaceholders(outputPath, answer);
        return outputPath.toFile();
    }

    private void replacePlaceholders(Path docxPath, AnswerSheetResult answer) throws IOException {
        URI docxUri = URI.create("jar:" + docxPath.toUri());
        try (java.nio.file.FileSystem fileSystem = java.nio.file.FileSystems.newFileSystem(docxUri, Map.of())) {
            Path documentXml = fileSystem.getPath(MAIN_DOCUMENT_PATH);
            String content = Files.readString(documentXml);
            content = content
                    .replace("{{nome}}", escapeXml(answer.getStudent().getName().toUpperCase()))
                    .replace("{{%}}", scoreText(answer))
                    .replace("{{data}}", LocalDate.now().format(DATE_FORMATTER));
            Files.writeString(documentXml, content);
        }
    }

    private String scoreText(AnswerSheetResult answer) {
        if (answer.getEvaluations() == null || answer.getEvaluations().isEmpty()) {
            return "";
        }

        AnswerSheetEvaluation evaluation = answer.getEvaluations().getFirst();
        return Math.round(evaluation.getScore()) + "%";
    }

    private String escapeXml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private String sanitizeFileName(String value) {
        return value == null || value.isBlank()
                ? "certificate"
                : value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
