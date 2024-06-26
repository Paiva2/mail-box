package com.root.mailbox.domain.exceptions.email;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EmailNotFoundException extends RuntimeException {
    private static final String MESSAGE = "E-mail not found";

    public EmailNotFoundException() {
        super(MESSAGE);
    }
}
