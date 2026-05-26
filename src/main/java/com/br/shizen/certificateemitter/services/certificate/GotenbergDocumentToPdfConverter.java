package com.br.shizen.certificateemitter.services.certificate;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class GotenbergDocumentToPdfConverter implements DocumentToPdfConverter {

    private static final String LIBRE_OFFICE_CONVERT_ENDPOINT = "/forms/libreoffice/convert";

    private final RestClient gotenbergRestClient;

    @Override
    public File convert(File sourceDocument) throws IOException {
        validateSourceDocument(sourceDocument);

        byte[] pdfContent = requestPdf(sourceDocument);
        Path outputPath = pdfOutputPath(sourceDocument.toPath());
        Files.write(outputPath, pdfContent);
        return outputPath.toFile();
    }

    private byte[] requestPdf(File sourceDocument) throws IOException {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("files", new FileSystemResource(sourceDocument));

        try {
            byte[] pdfContent = gotenbergRestClient.post()
                    .uri(LIBRE_OFFICE_CONVERT_ENDPOINT)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(byte[].class);

            if (pdfContent == null || pdfContent.length == 0) {
                throw new IOException("Gotenberg returned an empty PDF response");
            }
            return pdfContent;
        } catch (RestClientException e) {
            throw new IOException("Could not convert document to PDF using Gotenberg", e);
        }
    }

    private void validateSourceDocument(File sourceDocument) throws IOException {
        if (sourceDocument == null) {
            throw new IOException("Source document is required for PDF conversion");
        }
        if (!sourceDocument.isFile()) {
            throw new IOException("Source document does not exist: " + sourceDocument);
        }
    }

    private Path pdfOutputPath(Path sourcePath) {
        String fileName = sourcePath.getFileName().toString();
        int extensionIndex = fileName.lastIndexOf('.');
        String baseName = extensionIndex > 0
                ? fileName.substring(0, extensionIndex)
                : fileName;
        return sourcePath.resolveSibling(baseName + ".pdf");
    }
}
