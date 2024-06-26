package com.root.mailbox.presentation.controllers.folder;

import com.root.mailbox.presentation.dto.folder.CreateFolderInputDTO;
import com.root.mailbox.presentation.dto.folder.FolderOutputDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/folder")
public interface FolderController {
    @PostMapping("/new")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<FolderOutputDTO> create(Authentication authentication, CreateFolderInputDTO dto);
}
