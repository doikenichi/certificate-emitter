package com.br.shizen.certificateemitter.services;

import com.br.shizen.certificateemitter.dto.EmailTemplate;
import com.br.shizen.certificateemitter.google.GoogleDrive;
import com.br.shizen.certificateemitter.google.domain.GoogleDocsMimeType;
import com.br.shizen.certificateemitter.google.domain.QueryType;
import com.google.api.services.drive.model.File;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EmailTemplateService {
    private final GoogleDrive googleDrive;
    private final TempPathService tempPath;

    @Setter
    @Getter
    private String emailTemplateName;
    private String emailTemplateLocalname;

    public String createHtmlBody(String name) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(this.emailTemplateLocalname))) {
            return stream.map(line -> line.replace("{{nome}}", name)).collect(Collectors.joining());
        }
    }

    public EmailTemplate loadTemplate(String templateName) throws IOException {
        Map<QueryType, String> queryMap = new HashMap<>();
        queryMap.put(QueryType.NAME, templateName);
        queryMap.put(QueryType.MIME_TYPE, GoogleDocsMimeType.HTML.toString());
        List<File> result = this.googleDrive.getListExcludeFromTrash(queryMap);
        if (result.isEmpty()) {
            throw new IllegalStateException("Email template not found: " + templateName);
        }

        String emailTemplateId = String.valueOf(result.getFirst().get("id"));
        String localName = this.tempPath.getTempPath() + templateName;
        this.googleDrive.downloadGoogleDrive(localName, emailTemplateId);

        String content;
        try (Stream<String> stream = Files.lines(Paths.get(localName))) {
            content = stream.collect(Collectors.joining());
        }
        return new EmailTemplate(templateName, localName, content);
    }

}
