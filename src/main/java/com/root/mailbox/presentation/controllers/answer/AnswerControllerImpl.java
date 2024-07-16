package com.root.mailbox.presentation.controllers.answer;

import com.root.mailbox.domain.answer.CreateAnswerUsecase;
import com.root.mailbox.presentation.dto.answer.NewAnswerInputDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class AnswerControllerImpl implements AnswerController {
    private final CreateAnswerUsecase createAnswerUsecase;

    @Override
    public ResponseEntity<Void> create(
        Authentication authentication,
        @PathVariable("emailId") UUID emailId,
        @RequestBody @Valid NewAnswerInputDTO dto
    ) {
        Long userId = Long.valueOf(authentication.getName());
        createAnswerUsecase.exec(userId, emailId, dto.toAnswer());

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
