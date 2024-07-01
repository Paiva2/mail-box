package com.root.mailbox.presentation.controllers.attachment;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    ResponseEntity<Void> uploadAttachments(Authentication authentication, UUID emailId, List<MultipartFile> attachments);
}
