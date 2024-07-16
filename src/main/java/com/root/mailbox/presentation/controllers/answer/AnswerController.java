package com.root.mailbox.presentation.controllers.answer;

import com.root.mailbox.presentation.dto.answer.NewAnswerInputDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/answer")
public interface AnswerController {
    @PostMapping("/new/{emailId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<Void> create(Authentication authentication, UUID emailId, NewAnswerInputDTO dto);
}
