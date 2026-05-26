package com.br.shizen.certificateemitter.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

class GotenbergPropertiesTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfiguration.class);

    @Test
    void bindsConfiguredBaseUrlFromGotenbergPrefix() {
        contextRunner
                .withPropertyValues("gotenberg.base-url=http://gotenberg:3000")
                .run(context -> assertThat(context.getBean(GotenbergProperties.class).baseUrl())
                        .isEqualTo("http://gotenberg:3000"));
    }

    @Test
    void bindsMissingBaseUrlAsNullWhenNoPropertyIsConfigured() {
        contextRunner.run(context -> assertThat(context.getBean(GotenbergProperties.class).baseUrl())
                .isNull());
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(GotenbergProperties.class)
    private static class TestConfiguration {
    }
}
