package com.root.mailbox.domain.exceptions.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RecoverEmailAlreadyExistsException extends RuntimeException {
    private static final String MESSAGE = "An user with this recover e-mail already exists";

    public RecoverEmailAlreadyExistsException() {
        super(MESSAGE);
    }
}
