package com.root.mailbox.domain.exceptions.folder;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.MessageFormat;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FolderNotFoundException extends RuntimeException {
    private final static String MESSAGE = "Folder with identifier {0} not found.";

    public FolderNotFoundException(Long folderId) {
        super(MessageFormat.format(MESSAGE, folderId));
    }

}
