package com.root.mailbox.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyHasContactException extends RuntimeException {
    public UserAlreadyHasContactException() {
        super("User already has an contact with this name registered");
    }
}
