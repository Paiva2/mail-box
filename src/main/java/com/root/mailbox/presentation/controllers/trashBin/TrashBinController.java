package com.root.mailbox.presentation.controllers.trashBin;

import com.root.mailbox.presentation.dto.email.ListTrashEmailsOutputDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trashbin")
public interface TrashBinController {
    @PatchMapping("/me/email/{emailId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<Void> moveToTrash(Authentication authentication, UUID emailId);

    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<ListTrashEmailsOutputDTO> getTrash(Authentication authentication, Integer page, Integer size, String keyword, Boolean spam, Boolean opened);

    @DeleteMapping("/me/delete/email/{emailId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<Void> deleteFromTrash(Authentication authentication, UUID emailId);

    @PatchMapping("/me/recover/email/{emailId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<Void> recoverFromTrash(Authentication authentication, UUID emailId);
}
