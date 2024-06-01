package com.root.mailbox.presentation.controllers.email;

import com.root.mailbox.domain.usecases.email.NewEmailUsecase;
import com.root.mailbox.presentation.dto.email.NewEmailInputDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class EmailControllerImpl implements EmailController {
    private NewEmailUsecase newEmailUsecase;

    @Override
    public ResponseEntity<Void> create(
        Authentication authentication,
        @RequestBody @Valid NewEmailInputDTO dto
    ) {
        Long userId = Long.valueOf(authentication.getName());
        newEmailUsecase.exec(dto.toEmail(), userId);

        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }
}
