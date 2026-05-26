package com.br.shizen.certificateemitter.services.answersheet;

import com.br.shizen.certificateemitter.google.GoogleDrive;
import com.br.shizen.certificateemitter.google.domain.GoogleDocsMimeType;
import com.br.shizen.certificateemitter.google.domain.GoogleDriveMimeType;
import com.br.shizen.certificateemitter.google.domain.QueryType;
import com.br.shizen.certificateemitter.services.TempPathService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnswerSheetProvider {
    @Setter
    @Getter
    private String formAnswersName;

    private final GoogleDrive googleDrive;
    private final TempPathService tempPath;


    public File download(String answerSheetName) throws IOException {
        Map<QueryType, String> queryMap = new HashMap<>();
        // 1 - get form answers file id
        queryMap.put(QueryType.NAME, answerSheetName);
        queryMap.put(QueryType.MIME_TYPE, GoogleDriveMimeType.SHEETS.toString());
        List<com.google.api.services.drive.model.File> result = this.googleDrive.getListExcludeFromTrash(queryMap);
        String answersFormId = String.valueOf(result.getFirst().get("id"));
        Path tempDirectory = Path.of(this.tempPath.getTempPath());
        Files.createDirectories(tempDirectory);
        Path localFilePath = Files.createTempFile(tempDirectory, sanitizeFileName(answerSheetName) + "-", ".xlsx");

        try {
            this.googleDrive.downloadGoogleDocs(localFilePath.toString(), answersFormId, GoogleDocsMimeType.MS_EXCEL, null);
            return localFilePath.toFile();
        } catch (IOException e) {
            Files.deleteIfExists(localFilePath);
            throw e;
        }
    }

    private String sanitizeFileName(String fileName) {
        String sanitized = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        return sanitized.length() >= 3 ? sanitized : "answers";
    }
}
