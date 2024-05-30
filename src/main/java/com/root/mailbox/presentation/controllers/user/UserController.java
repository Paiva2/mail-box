package com.root.mailbox.presentation.controllers.user;

import com.root.mailbox.presentation.dto.user.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
public interface UserController {
    @PostMapping("/register")
    ResponseEntity<RegisterUserOutputDTO> register(RegisterUserInputDTO dto);

    @PostMapping("/login")
    ResponseEntity<Map<String, String>> login(AuthenticateUserInputDTO dto);

    @GetMapping("/profile")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<GetUserProfileOutputDTO> profile(Authentication authentication);

    @PatchMapping("/update")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<GetUserProfileOutputDTO> update(Authentication authentication, UpdateUserProfileInputDTO dto);
}
