package com.root.mailbox.presentation.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class JwtCreationException extends RuntimeException {
    public JwtCreationException() {
        super("Error while generating JWT token");
    }
}
