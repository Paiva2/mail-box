package com.root.mailbox.domain.exceptions.email;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmailNonDraftException extends RuntimeException {
    private final static String MESSAGE = "E-mail is not a draft";

    public EmailNonDraftException() {
        super(MESSAGE);
    }
}
