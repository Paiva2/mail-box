package com.root.mailbox.domain.exceptions.trashBinUserEmail;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EmailAlreadyAddedOnTrashBinException extends RuntimeException {
    private static final String MESSAGE = "E-mail is already added on trash bin";

    public EmailAlreadyAddedOnTrashBinException() {
        super(MESSAGE);
    }
}
