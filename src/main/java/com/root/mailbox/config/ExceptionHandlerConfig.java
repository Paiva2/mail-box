package com.root.mailbox.config;

import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.presentation.dto.exception.ArgumentNotValidExceptionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class ExceptionHandlerConfig {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> argumentsHandlerException(MethodArgumentNotValidException ex) {
        Map<String, Object> errorMap = new LinkedHashMap<>() {{
            put("status", HttpStatus.UNPROCESSABLE_ENTITY.value());
            put("exception", ex.getDetailMessageCode());
        }};

        List<ArgumentNotValidExceptionDTO> errors = ex.getFieldErrors().stream()
            .map(err -> new ArgumentNotValidExceptionDTO(err.getField(), err.getDefaultMessage()))
            .toList();

        errorMap.put("errors", errors);


        return new ResponseEntity<>(errorMap, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> argumentsHandlerException(UserNotFoundException ex) {
        Map<String, Object> errorMap = new LinkedHashMap<>() {{
            put("status", HttpStatus.NOT_FOUND.value());
            put("exception", ex.getMessage());
        }};

        return new ResponseEntity<>(errorMap, HttpStatus.NOT_FOUND);
    }
}
