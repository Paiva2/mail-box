package com.root.mailbox.presentation.controllers.user;

import com.root.mailbox.domain.usecases.user.AuthenticateUserUsecase;
import com.root.mailbox.domain.usecases.user.GetUserProfileUsecase;
import com.root.mailbox.domain.usecases.user.RegisterUserUsecase;
import com.root.mailbox.presentation.adapters.JwtAdapter;
import com.root.mailbox.presentation.dto.jwt.GenerateJwtDto;
import com.root.mailbox.presentation.dto.user.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@AllArgsConstructor
public class UserControllerImpl implements UserController {
    private final RegisterUserUsecase registerUserUsecase;
    private final AuthenticateUserUsecase authenticateUserUsecase;
    private final GetUserProfileUsecase getUserProfileUsecase;

    private final JwtAdapter jwtAdapter;

    @Override
    public ResponseEntity<RegisterUserOutputDTO> register(
        @RequestBody @Valid RegisterUserInputDTO dto
    ) {
        RegisterUserOutputDTO output = registerUserUsecase.exec(dto.toUser());

        return new ResponseEntity<>(output, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Map<String, String>> login(
        @RequestBody @Valid AuthenticateUserInputDTO dto
    ) {
        AuthenticateUserOutputDTO output = authenticateUserUsecase.exec(dto.toUser());

        String token = jwtAdapter.generate(GenerateJwtDto.builder()
            .id(output.getId())
            .role(output.getRole().name())
            .build()
        );

        return new ResponseEntity<>(Collections.singletonMap("authToken", token), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<GetUserProfileOutputDTO> profile(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        GetUserProfileOutputDTO output = getUserProfileUsecase.exec(userId);

        return new ResponseEntity<>(output, HttpStatus.OK);
    }
}
