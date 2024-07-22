package com.root.mailbox.presentation.controllers.user;

import com.root.mailbox.domain.usecases.user.*;
import com.root.mailbox.presentation.adapters.JwtAdapter;
import com.root.mailbox.presentation.dto.jwt.GenerateJwtDto;
import com.root.mailbox.presentation.dto.user.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Map;

@RestController
@AllArgsConstructor
public class UserControllerImpl implements UserController {
    private final RegisterUserUsecase registerUserUsecase;
    private final AuthenticateUserUsecase authenticateUserUsecase;
    private final GetUserProfileUsecase getUserProfileUsecase;
    private final UpdateUserProfileUsecase updateUserProfileUsecase;
    private final ForgotPasswordUsecase forgotPasswordUsecase;
    private final UploadProfilePictureUsecase uploadProfilePictureUsecase;

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

    @Override
    public ResponseEntity<Map<String, String>> uploadProfilePicture(
        Authentication authentication,
        @RequestParam MultipartFile picture
    ) {
        Long userId = Long.valueOf(authentication.getName());
        String url = uploadProfilePictureUsecase.exec(userId, picture);

        return new ResponseEntity<>(Collections.singletonMap("profilePictureUrl", url), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> forgotPassword(
        @RequestBody @Valid ForgotPasswordInputDTO dto
    ) {
        forgotPasswordUsecase.exec(dto.getEmail());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<GetUserProfileOutputDTO> update(
        Authentication authentication,
        @RequestBody @Valid UpdateUserProfileInputDTO dto
    ) {
        Long userId = Long.valueOf(authentication.getName());
        GetUserProfileOutputDTO output = updateUserProfileUsecase.exec(dto.toUser(userId));

        return new ResponseEntity<>(output, HttpStatus.OK);
    }
}
