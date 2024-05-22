package com.root.mailbox.presentation.dto.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ArgumentNotValidExceptionDTO {
    private String field;
    private String message;
}
