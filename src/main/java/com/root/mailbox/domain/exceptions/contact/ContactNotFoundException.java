package com.root.mailbox.domain.exceptions.contact;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.MessageFormat;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ContactNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Contact with identifier {0} not found";

    public ContactNotFoundException(String identifier) {
        super(MessageFormat.format(MESSAGE, identifier));
    }
}
