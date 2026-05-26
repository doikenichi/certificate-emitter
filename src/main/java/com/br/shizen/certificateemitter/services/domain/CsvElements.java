package com.br.shizen.certificateemitter.services.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Getter
@Setter
@AllArgsConstructor
public class CsvElements {
    String name;
    String email;
    String certificatePath;

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JacksonException e) {
            return "{" + "\"name\":\"'" + this.name + "\"," +
                    "\"email\":\"" + this.email + "\"," +
                    "\"certificatePah\":\"" + this.certificatePath + "\"" +
                    '}';
        }
    }
}
