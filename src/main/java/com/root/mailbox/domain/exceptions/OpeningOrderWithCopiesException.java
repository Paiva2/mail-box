package com.root.mailbox.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class OpeningOrderWithCopiesException extends RuntimeException {
    public OpeningOrderWithCopiesException() {
        super("E-mail with opening orders can't have users on copy");
    }
}
