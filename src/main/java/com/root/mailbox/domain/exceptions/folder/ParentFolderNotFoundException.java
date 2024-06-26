package com.root.mailbox.domain.exceptions.folder;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.MessageFormat;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ParentFolderNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Parent folder with identifier {0} not found.";

    public ParentFolderNotFoundException(Long identifier) {
        super(MessageFormat.format(MESSAGE, identifier));
    }
}
