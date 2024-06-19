package com.root.mailbox.domain.exceptions.trashBinUserEmail;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserEmailNotOnTrashException extends RuntimeException {
    public UserEmailNotOnTrashException() {
        super("E-mail must be on Trash before complete deletion");
    }
}
