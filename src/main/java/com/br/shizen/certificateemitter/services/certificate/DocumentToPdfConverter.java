package com.br.shizen.certificateemitter.services.certificate;

import java.io.File;
import java.io.IOException;

public interface DocumentToPdfConverter {
    File convert(File sourceDocument) throws IOException;
}
