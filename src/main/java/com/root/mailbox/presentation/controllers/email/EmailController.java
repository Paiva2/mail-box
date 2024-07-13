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
    ResponseEntity<EmailOutputDTO> create(Authentication authentication, NewEmailInputDTO dto);

    @PostMapping("/new/draft")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<Void> createDraft(Authentication authentication, NewDraftEmailInputDTO dto);

    @GetMapping("/inbox")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<EmailInboxOutputDTO> getInbox(Authentication authentication, Integer page, Integer size, String keyword, Boolean filteringSpam, Boolean opened, String flag);

    @GetMapping("/me/{emailId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<EmailOutputDTO> getEmailToMe(Authentication authentication, UUID emailId);

    @GetMapping("/sent")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<EmailsSentPaginationOutputDTO> getSent(Authentication authentication, Integer page, Integer size, String keyword);

    @GetMapping("/drafts")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<ListDraftEmailsOutputDTO> getDrafts(Authentication authentication, Integer page, Integer size, String keyword);

    @GetMapping("/sent/{emailId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<EmailSentOutputDTO> filterSent(Authentication authentication, UUID emailId);

    @PatchMapping("/spam/{emailId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<EmailOutputDTO> handleSpam(Authentication authentication, UUID emailId, Boolean setSpam);

    @PatchMapping("/unopen/{emailId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<EmailOutputDTO> unopenEmail(Authentication authentication, UUID emailId);

    @DeleteMapping("/draft/delete/email/{emailId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<Void> deleteDraft(Authentication authentication, UUID emailId);

    @PatchMapping("/draft/send/email/{emailId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<Void> sendDraft(Authentication authentication, UUID emailId, SendDraftEmailInputDTO dto);

    @PatchMapping("/{emailId}/folder/change")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<Void> updateFolder(Authentication authentication, UUID emailId, UpdateFolderInputDTO dto);

    @PatchMapping("/{emailId}/flag")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<Void> updateFlag(Authentication authentication, UUID emailId, String flag);
}
