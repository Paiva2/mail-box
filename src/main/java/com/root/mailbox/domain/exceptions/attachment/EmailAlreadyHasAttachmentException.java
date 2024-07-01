package com.root.mailbox.domain.exceptions.attachment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EmailAlreadyHasAttachmentException extends RuntimeException {
    private static final String MESSAGE = "Can't insert attachments on e-mail that already has attachments.";

    public EmailAlreadyHasAttachmentException() {
        super(MESSAGE);
    }
}
