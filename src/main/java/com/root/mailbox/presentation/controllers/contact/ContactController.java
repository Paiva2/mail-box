package com.root.mailbox.presentation.controllers.contact;

import com.root.mailbox.presentation.dto.contact.ContactOutputDTO;
import com.root.mailbox.presentation.dto.contact.CreateContactInputDTO;
import com.root.mailbox.presentation.dto.contact.ListContactsPaginationOutputDTO;
import com.root.mailbox.presentation.dto.contact.UpdateContactInputDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contact")
public interface ContactController {

    @PostMapping("/new")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<ContactOutputDTO> create(Authentication authentication, CreateContactInputDTO dto);

    @GetMapping("/list")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<ListContactsPaginationOutputDTO> listAll(Authentication authentication, Integer page, Integer size, String name);

    @GetMapping("/{contactId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<ContactOutputDTO> filter(Authentication authentication, Long contactId);

    @DeleteMapping("/{contactId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<Void> delete(Authentication authentication, Long contactId);

    @PatchMapping("/{contactId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<ContactOutputDTO> update(Authentication authentication, Long contactId, UpdateContactInputDTO dto);
}
