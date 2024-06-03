package com.root.mailbox.presentation.controllers.email;

import com.root.mailbox.presentation.dto.email.ListInboxOutputDTO;
import com.root.mailbox.presentation.dto.email.NewEmailInputDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/email")
public interface EmailController {

    @PostMapping("/new")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<Void> create(Authentication authentication, NewEmailInputDTO dto);

    @GetMapping("/inbox")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<ListInboxOutputDTO> getInbox(Authentication authentication, Integer page, Integer size, String keyword);
}
