package com.root.mailbox.presentation.controllers.attachment;

import com.root.mailbox.presentation.dto.attachment.DownloadAttachmentOutputDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/v1/attachment")
@RestController
public interface AttachmentController {
    @PostMapping(value = "/insert/email/{emailId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<Void> uploadAttachments(Authentication authentication, UUID emailId, List<MultipartFile> attachments);

    @GetMapping(value = "/{attachmentId}/email/{emailId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<DownloadAttachmentOutputDTO> getAttachmentDownload(Authentication authentication, UUID attachmentId, UUID emailId);
}
