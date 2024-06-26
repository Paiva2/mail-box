package com.root.mailbox.presentation.controllers.folder;

import com.root.mailbox.presentation.dto.email.EmailInboxOutputDTO;
import com.root.mailbox.presentation.dto.folder.CreateFolderInputDTO;
import com.root.mailbox.presentation.dto.folder.FolderOutputDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/folder")
public interface FolderController {
    @PostMapping("/new")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<FolderOutputDTO> create(Authentication authentication, CreateFolderInputDTO dto);

    @GetMapping("/list/root")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<List<FolderOutputDTO>> listRoot(Authentication authentication);

    @GetMapping("/{folderId}/children")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<List<FolderOutputDTO>> listChildren(Authentication authentication, Long folderId);

    @PostMapping("/email/{emailId}/insert/{folderId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<Void> insertEmail(Authentication authentication, UUID emailId, Long folderId);

    @GetMapping("/{folderId}/emails")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<EmailInboxOutputDTO> listFolderEmails(Authentication authentication, Long folderId, Integer page, Integer size, String keyword);
}
