package com.br.shizen.certificateemitter.services.certificate;

import com.br.shizen.certificateemitter.google.GoogleDrive;
import com.br.shizen.certificateemitter.google.domain.GoogleDocsMimeType;
import com.br.shizen.certificateemitter.google.domain.QueryType;
import com.br.shizen.certificateemitter.services.TempPathService;
import com.google.api.services.drive.model.File;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CertificateProvider {

    private final GoogleDrive googleDrive;
    private final TempPathService tempPath;

    public CertificateTemplate download(String certificateTemplateName) throws IOException {
        Map<QueryType, String> queryMap = new HashMap<>();
        queryMap.put(QueryType.NAME, certificateTemplateName);
        queryMap.put(QueryType.MIME_TYPE, GoogleDocsMimeType.MS_WORD.toString());

        List<File> result = googleDrive.getListExcludeFromTrash(queryMap);
        if (result.isEmpty()) {
            throw new IllegalStateException("Certificate template not found: " + certificateTemplateName);
        }

        String certificateTemplateFileId = String.valueOf(result.getFirst().get("id"));
        String certificateLocalPath = tempPath.getTempPath() + certificateTemplateName;
        googleDrive.downloadGoogleDrive(certificateLocalPath, certificateTemplateFileId);

        return new CertificateTemplate(certificateTemplateName, certificateLocalPath, certificateTemplateFileId);
    }
}
