package com.br.shizen.certificateemitter.google;

import com.br.shizen.certificateemitter.google.domain.GoogleDocsMimeType;
import com.br.shizen.certificateemitter.google.domain.GoogleDriveMimeType;
import com.br.shizen.certificateemitter.google.domain.QueryType;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files.Export;
import com.google.api.services.drive.Drive.Files.Get;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GoogleDrive extends Google {
    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private Drive googleDriveService;

    @Override
    public void initialize() throws IOException, GeneralSecurityException {
        final HttpTransport transport = new NetHttpTransport.Builder().build();
        this.googleDriveService = new Drive.Builder(transport, JSON_FACTORY, getCredentials(transport))
                .setApplicationName(APPLICATION_NAME)
                .build();
        this.googleDriveService.files().emptyTrash();
    }

    public FileList getList(@NonNull Map<QueryType, String> queryTerms) throws IOException {
        StringBuilder sb = new StringBuilder();
        queryTerms.forEach((queryType, searchTerm) -> {
            if (!sb.isEmpty()) {
                sb.append(" and ");
            }
            sb.append(queryType.toString()).append("=").append("'").append(searchTerm).append("'");
        });
        return this.googleDriveService.files().list()
                .setQ(sb.toString())
                .setPageSize(100)
                .setFields("nextPageToken,files(id,name,mimeType,explicitlyTrashed,trashed)")
//                .setFields("nextPageToken,files(*)") // this is for debug only
                .execute();
    }

    public List<File> getListExcludeFromTrash(Map<QueryType, String> queryTerms) throws IOException {
        FileList result = this.getList(queryTerms);
        return result.getFiles().stream()
                .filter(file -> !((Boolean) file.get("trashed")) && !((Boolean) file.get("explicitlyTrashed")))
                .collect(Collectors.toList());
    }

    public void downloadGoogleDocs(String fullPath, String fileId, GoogleDocsMimeType docsMimeType, GoogleDriveMimeType driveMimeType) throws IOException {
        try (OutputStream outputStream = new FileOutputStream(fullPath)) {
            Export export = driveMimeType == null
                    ? this.googleDriveService.files().export(fileId, docsMimeType.toString())
                    : this.googleDriveService.files().export(fileId, driveMimeType.toString());
            export.getMediaHttpDownloader().setDirectDownloadEnabled(true);
            export.executeMediaAndDownloadTo(outputStream);
            outputStream.flush();
        }
    }

    public void downloadGoogleDrive(String fullPath, String fileId) throws IOException {
        try (OutputStream outputStream = new FileOutputStream(fullPath)) {
            Get get = this.googleDriveService.files().get(fileId);
            get.getMediaHttpDownloader().setDirectDownloadEnabled(true);
            get.executeMediaAndDownloadTo(outputStream);
            outputStream.flush();
        }
    }

    public void uploadFile(String fileName, String path, @NonNull GoogleDocsMimeType googleDocsMimeType, String gdriveFileId) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(fileName);
        java.io.File filePath = new java.io.File(path);
        FileContent mediaContent = new FileContent(googleDocsMimeType.toString(), filePath);
        if (gdriveFileId == null) {
            this.googleDriveService.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();
        } else {
            this.googleDriveService.files().update(gdriveFileId, fileMetadata, mediaContent).execute();
        }
    }
}
