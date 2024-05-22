package com.root.mailbox.presentation.controllers.user;

import com.root.mailbox.presentation.dto.user.RegisterUserInputDTO;
import com.root.mailbox.presentation.dto.user.RegisterUserOutputDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public interface UserController {
    @PostMapping("/register")
    ResponseEntity<RegisterUserOutputDTO> register(RegisterUserInputDTO dto);
}
