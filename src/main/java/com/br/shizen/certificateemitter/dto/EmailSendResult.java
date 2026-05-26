package com.br.shizen.certificateemitter.dto;

import java.util.List;

public record EmailSendResult(
        int emailsAttempted,
        int emailsSent,
        int emailsSkipped,
        List<ImportError> errors
) {
}
