package com.root.mailbox.domain.exceptions.email;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.MessageFormat;

@ResponseStatus(HttpStatus.CONFLICT)
public class EmailDisabledException extends RuntimeException {
    private final static String MESSAGE = "E-mail with identifier {0} is disabled";

    public EmailDisabledException(String emailId) {
        super(MessageFormat.format(MESSAGE, emailId));
    }
}
