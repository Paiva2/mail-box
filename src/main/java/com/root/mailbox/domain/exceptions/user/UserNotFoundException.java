package com.root.mailbox.domain.exceptions.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.MessageFormat;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    private static final String MESSAGE = "User with identifier {0} not found";

    public UserNotFoundException(String identifier) {
        super(MessageFormat.format(MESSAGE, identifier));
    }
}
