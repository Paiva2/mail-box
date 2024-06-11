package com.root.mailbox.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RepeatedUsersToOrCopyException extends RuntimeException {
    public RepeatedUsersToOrCopyException() {
        super("Can't repeat users on usersTo list or copy list");
    }
}
