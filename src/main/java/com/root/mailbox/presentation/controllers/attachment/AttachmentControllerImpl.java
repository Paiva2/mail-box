package com.root.mailbox.presentation.controllers.attachment;

import com.root.mailbox.domain.usecases.attachment.InsertAttachmentsUsecase;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class AttachmentControllerImpl implements AttachmentController {
    private final InsertAttachmentsUsecase insertAttachmentsUsecase;

    @Override
    public ResponseEntity<Void> uploadAttachments(
        Authentication authentication,
        @PathVariable("emailId") UUID emailId,
        @RequestParam(value = "attachments", required = true) List<MultipartFile> attachments) {
        Long userId = Long.valueOf(authentication.getName());

        insertAttachmentsUsecase.exec(userId, emailId, attachments);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
