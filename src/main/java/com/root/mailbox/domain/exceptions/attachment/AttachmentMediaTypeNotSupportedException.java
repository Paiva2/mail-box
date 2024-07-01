package com.root.mailbox.domain.exceptions.attachment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.MessageFormat;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AttachmentMediaTypeNotSupportedException extends RuntimeException {
    private final static String MESSAGE = "File {0} has an Media Type not supported. Supported extensions are: JPEG, JPG, PDF, XLSX, XLS, CSV, TXT.";

    public AttachmentMediaTypeNotSupportedException(String fileName) {
        super(MessageFormat.format(MESSAGE, fileName));
    }
}
