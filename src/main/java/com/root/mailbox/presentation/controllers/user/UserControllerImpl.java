package com.root.mailbox.presentation.controllers.user;

import com.root.mailbox.domain.usecases.user.RegisterUserUsecase;
import com.root.mailbox.presentation.dto.user.RegisterUserInputDTO;
import com.root.mailbox.presentation.dto.user.RegisterUserOutputDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserControllerImpl implements  UserController{
    private final RegisterUserUsecase registerUserUsecase;

    @Override
    public ResponseEntity<RegisterUserOutputDTO> register(
    @RequestBody @Valid RegisterUserInputDTO dto
    ) {
        RegisterUserOutputDTO output = registerUserUsecase.exec(dto.toUser());

        return new ResponseEntity<>(output, HttpStatus.CREATED);
    }
}
