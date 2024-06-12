package com.root.mailbox.domain.exceptions.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class WeakPasswordException extends RuntimeException {
    private static final String MESSAGE = "Password must have at least 6 characters";

    public WeakPasswordException() {
        super(MESSAGE);
    }
}
