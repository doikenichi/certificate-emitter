package com.br.shizen.certificateemitter.web.rest.controller;

import com.br.shizen.certificateemitter.dto.CertificateEmissionResult;
import com.br.shizen.certificateemitter.dto.CertificateImportRequest;
import com.br.shizen.certificateemitter.services.CertificateEmissionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CertificateApiController.class)
class CertificateApiControllerTests {

    private static final String IMPORT_URL = "/api/certificates/import";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CertificateEmissionService certificateEmissionService;

    @Test
    void executeDelegatesValidRequestAndReturnsEmissionResult() throws Exception {
        CertificateEmissionResult result = new CertificateEmissionResult(
                3,
                2,
                1,
                1,
                1,
                4,
                2,
                2,
                List.of()
        );
        when(certificateEmissionService.emit(any(CertificateImportRequest.class))).thenReturn(result);

        mockMvc.perform(post(IMPORT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "formAnswersName": "answers.xlsx",
                                  "certificateTemplate": "certificate-template.docx",
                                  "emailTemplateName": "completion-email.html"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rowsRead").value(3))
                .andExpect(jsonPath("$.rowsImported").value(2))
                .andExpect(jsonPath("$.rowsSkipped").value(1))
                .andExpect(jsonPath("$.studentsCreated").value(1))
                .andExpect(jsonPath("$.takesCreated").value(1))
                .andExpect(jsonPath("$.responsesCreated").value(4))
                .andExpect(jsonPath("$.responsesUpdated").value(2))
                .andExpect(jsonPath("$.certificatesGenerated").value(2))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(certificateEmissionService).emit(new CertificateImportRequest(
                "answers.xlsx",
                "certificate-template.docx",
                "completion-email.html"
        ));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            """
                    {
                      "formAnswersName": "",
                      "certificateTemplate": "certificate-template.docx",
                      "emailTemplateName": "completion-email.html"
                    }
                    """,
            """
                    {
                      "formAnswersName": " ",
                      "certificateTemplate": "certificate-template.docx",
                      "emailTemplateName": "completion-email.html"
                    }
                    """,
            """
                    {
                      "certificateTemplate": "certificate-template.docx",
                      "emailTemplateName": "completion-email.html"
                    }
                    """,
            """
                    {
                      "formAnswersName": "answers.xlsx",
                      "certificateTemplate": "",
                      "emailTemplateName": "completion-email.html"
                    }
                    """,
            """
                    {
                      "formAnswersName": "answers.xlsx",
                      "certificateTemplate": " ",
                      "emailTemplateName": "completion-email.html"
                    }
                    """,
            """
                    {
                      "formAnswersName": "answers.xlsx",
                      "emailTemplateName": "completion-email.html"
                    }
                    """,
            """
                    {
                      "formAnswersName": "answers.xlsx",
                      "certificateTemplate": "certificate-template.docx",
                      "emailTemplateName": ""
                    }
                    """,
            """
                    {
                      "formAnswersName": "answers.xlsx",
                      "certificateTemplate": "certificate-template.docx",
                      "emailTemplateName": " "
                    }
                    """,
            """
                    {
                      "formAnswersName": "answers.xlsx",
                      "certificateTemplate": "certificate-template.docx"
                    }
                    """
    })
    void executeRejectsBlankOrMissingRequiredFields(String requestBody) throws Exception {
        mockMvc.perform(post(IMPORT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(certificateEmissionService);
    }

    @Test
    void executeRejectsMissingRequestBody() throws Exception {
        mockMvc.perform(post(IMPORT_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(certificateEmissionService);
    }
}
