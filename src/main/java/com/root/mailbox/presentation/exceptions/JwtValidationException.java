package com.root.mailbox.presentation.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class JwtValidationException extends RuntimeException {
    public JwtValidationException(String reason) {
        super("Error while validating JWT, reason: " + reason);
    }
}
