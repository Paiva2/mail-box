package com.root.mailbox.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserToInCopyListException extends RuntimeException {
    public UserToInCopyListException() {
        super("Can't repeat a user that's present on usersTo list on copy list or vice-versa");
    }
}
