package com.br.shizen.certificateemitter.web.rest.controller;

import com.br.shizen.certificateemitter.dto.CertificateImportRequest;
import com.br.shizen.certificateemitter.dto.CertificateEmissionResult;
import com.br.shizen.certificateemitter.services.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/certificates")
@AllArgsConstructor
@Tag(name = "Certificate", description = "Certificate API")
public class CertificateApiController {

    private final CertificateEmissionService certificateEmissionService;

    @PostMapping("/import")
    public CertificateEmissionResult execute(@RequestBody @Valid CertificateImportRequest request) throws IOException {
        return certificateEmissionService.emit(request);
    }
}
