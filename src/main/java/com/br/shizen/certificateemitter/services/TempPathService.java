package com.br.shizen.certificateemitter.services;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TempPathService {
    @Getter
    @Setter
    @Value("${local.temp}")
    private String tempPath;
}