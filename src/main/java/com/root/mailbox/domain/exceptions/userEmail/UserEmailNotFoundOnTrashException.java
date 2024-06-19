package com.root.mailbox.domain.exceptions.userEmail;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserEmailNotFoundOnTrashException extends RuntimeException {
    public UserEmailNotFoundOnTrashException() {
        super("E-mail not found on trash");
    }
}
