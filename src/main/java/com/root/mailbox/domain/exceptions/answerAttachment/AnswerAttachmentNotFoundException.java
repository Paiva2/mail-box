package com.root.mailbox.domain.exceptions.answerAttachment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AnswerAttachmentNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Answer Attachment not found";

    public AnswerAttachmentNotFoundException() {
        super(MESSAGE);
    }
}
