package com.root.mailbox.domain.exceptions.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.MessageFormat;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserDisabledException extends RuntimeException {
    private final static String MESSAGE = "User with identifier {0} is disabled";

    public UserDisabledException(Long id) {
        super(MessageFormat.format(MESSAGE, id));
    }
}
