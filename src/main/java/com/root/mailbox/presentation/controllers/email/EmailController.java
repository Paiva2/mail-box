package com.root.mailbox.presentation.controllers.email;

import com.root.mailbox.presentation.dto.email.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/email")
public interface EmailController {

    @PostMapping("/new")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<Void> create(Authentication authentication, NewEmailInputDTO dto);

    @GetMapping("/inbox")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<ListInboxOutputDTO> getInbox(Authentication authentication, Integer page, Integer size, String keyword, Boolean filteringSpam, Boolean opened);

    @GetMapping("/me/{emailId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<EmailOutputDTO> getEmailToMe(Authentication authentication, UUID emailId);

    @GetMapping("/sent")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<EmailsSentPaginationOutputDTO> getSent(Authentication authentication, Integer page, Integer size, String keyword);

    @GetMapping("/sent/{emailId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<EmailSentOutputDTO> filterSent(Authentication authentication, UUID emailId);

    @PatchMapping("/spam/{emailId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<EmailOutputDTO> handleSpam(Authentication authentication, UUID emailId, Boolean setSpam);

    @PatchMapping("/unopen/{emailId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<EmailOutputDTO> unopenEmail(Authentication authentication, UUID emailId);

    @PatchMapping("/me/trash/{emailId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<Void> moveToTrash(Authentication authentication, UUID emailId);
}
