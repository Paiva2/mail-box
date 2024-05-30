package com.root.mailbox.presentation.controllers.contact;

import com.root.mailbox.presentation.dto.contact.ContactOutputDTO;
import com.root.mailbox.presentation.dto.contact.CreateContactInputDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/contact")
public interface ContactController {
    
    @PostMapping("/new")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<ContactOutputDTO> create(Authentication authentication, CreateContactInputDTO dto);
}
