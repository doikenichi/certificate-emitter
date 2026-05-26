package com.br.shizen.certificateemitter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gotenberg")
public record GotenbergProperties(String baseUrl) {
}
