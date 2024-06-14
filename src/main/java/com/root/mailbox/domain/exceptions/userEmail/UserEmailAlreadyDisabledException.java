package com.root.mailbox.domain.exceptions.userEmail;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserEmailAlreadyDisabledException extends RuntimeException {
    public UserEmailAlreadyDisabledException() {
        super("E-mail already on trash bin");
    }
}
