package com.root.mailbox.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.MessageFormat;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyExistsException extends RuntimeException {
    private static final String MESSAGE = "An user with this {0} already exists";

    public UserAlreadyExistsException(String property){
        super(MessageFormat.format(MESSAGE, property));
    }
}
