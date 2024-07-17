package com.root.mailbox.domain.exceptions.answer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AnswerNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Answer not found";

    public AnswerNotFoundException() {
        super(MESSAGE);
    }
}
