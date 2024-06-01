package com.root.mailbox.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.MessageFormat;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CarbonCopiesNotFoundException extends RuntimeException {
    private final static String MESSAGE = "Copied users {0} not found registered";

    public CarbonCopiesNotFoundException(String emailList) {
        super(MessageFormat.format(MESSAGE, emailList));
    }
}
