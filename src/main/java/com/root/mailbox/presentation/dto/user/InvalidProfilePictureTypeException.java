package com.root.mailbox.presentation.dto.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidProfilePictureTypeException extends RuntimeException {
    private final static String MESSAGE = "Invalid file content type. Valid formats are: JPEG, JPG, PNG.";

    public InvalidProfilePictureTypeException() {
        super(MESSAGE);
    }
}
