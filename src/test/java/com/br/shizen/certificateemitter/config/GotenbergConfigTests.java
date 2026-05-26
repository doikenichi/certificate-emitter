package com.br.shizen.certificateemitter.config;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilderFactory;

import static org.assertj.core.api.Assertions.assertThat;

class GotenbergConfigTests {

    @Test
    void gotenbergRestClientResolvesRelativeUrisAgainstConfiguredBaseUrl() {
        GotenbergConfig config = new GotenbergConfig();
        RestClient restClient = config.gotenbergRestClient(new GotenbergProperties("http://gotenberg:3000"));

        UriBuilderFactory uriBuilderFactory = (UriBuilderFactory) ReflectionTestUtils.getField(
                restClient,
                "uriBuilderFactory"
        );

        assertThat(uriBuilderFactory).isNotNull();
        assertThat(uriBuilderFactory.expand("/forms/libreoffice/convert").toString())
                .isEqualTo("http://gotenberg:3000/forms/libreoffice/convert");
    }
}
