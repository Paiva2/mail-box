package com.root.mailbox.domain.exceptions.attachment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.MessageFormat;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AttachmentMaxSizeExceeded extends RuntimeException {
    private final static String MESSAGE = "File {0} has more than the maximum size allowed (5MB).";

    public AttachmentMaxSizeExceeded(String fileName) {
        super(MessageFormat.format(MESSAGE, fileName));
    }
}
