package com.root.mailbox.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.MessageFormat;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserEmailNotFoundException extends RuntimeException {
    private static final String MESSAGE = "E-mail with identifier {0} and user identifier {1} not found";

    public UserEmailNotFoundException(String emailId, String userId) {
        super(MessageFormat.format(MESSAGE, emailId, userId));
    }
}
