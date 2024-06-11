package com.root.mailbox.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.MessageFormat;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class OpeningOrderNotFoundException extends RuntimeException {
    private final static String MESSAGE = "Opening order with user identifier {0} and e-mail identifier {1} not found";

    public OpeningOrderNotFoundException(String... identifiers) {
        super(MessageFormat.format(MESSAGE, identifiers[0], identifiers[1]));
    }
}
