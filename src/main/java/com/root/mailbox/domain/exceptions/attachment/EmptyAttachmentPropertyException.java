package com.root.mailbox.domain.exceptions.attachment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.MessageFormat;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class EmptyAttachmentPropertyException extends RuntimeException {
    private final static String MESSAGE = "{0} cannot be empty in the file";

    public EmptyAttachmentPropertyException(String property) {
        super(MessageFormat.format(MESSAGE, property));
    }
}
