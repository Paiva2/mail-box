package com.root.mailbox.domain.exceptions.email;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class SendingEmailToHimselfUsecase extends RuntimeException {
    private final static String MESSAGE = "Can't send a e-mail to himself.";

    public SendingEmailToHimselfUsecase() {
        super(MESSAGE);
    }
}
