package com.root.mailbox.presentation.controllers.user;

import com.root.mailbox.presentation.dto.user.AuthenticateUserInputDTO;
import com.root.mailbox.presentation.dto.user.GetUserProfileOutputDTO;
import com.root.mailbox.presentation.dto.user.RegisterUserInputDTO;
import com.root.mailbox.presentation.dto.user.RegisterUserOutputDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
public interface UserController {
    @PostMapping("/register")
    ResponseEntity<RegisterUserOutputDTO> register(RegisterUserInputDTO dto);

    @PostMapping("/login")
    ResponseEntity<Map<String, String>> login(AuthenticateUserInputDTO dto);

    @GetMapping("/profile")
    @PreAuthorize("ROLE_USER")
    ResponseEntity<GetUserProfileOutputDTO> profile(Authentication authentication);
}
