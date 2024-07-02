package com.root.mailbox.domain.exceptions.attachment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AttachmentNotFoundException extends RuntimeException {
    private final static String MESSAGE = "Attachment not found.";

    public AttachmentNotFoundException() {
        super(MESSAGE);
    }
}
