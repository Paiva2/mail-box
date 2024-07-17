package com.root.mailbox.presentation.controllers.attachment;

import com.root.mailbox.domain.usecases.attachment.DownloadAttachmentUsecase;
import com.root.mailbox.domain.usecases.attachment.InsertAnswerAttachmentsUsecase;
import com.root.mailbox.domain.usecases.attachment.InsertEmailAttachmentsUsecase;
import com.root.mailbox.presentation.dto.attachment.DownloadAttachmentOutputDTO;
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
    private final InsertEmailAttachmentsUsecase insertEmailAttachmentsUsecase;
    private final DownloadAttachmentUsecase downloadAttachmentUsecase;
    private final InsertAnswerAttachmentsUsecase insertAnswerAttachmentsUsecase;

    @Override
    public ResponseEntity<Void> uploadEmailAttachments(
        Authentication authentication,
        @PathVariable("emailId") UUID emailId,
        @RequestParam(value = "attachments", required = true) List<MultipartFile> attachments) {
        Long userId = Long.valueOf(authentication.getName());

        insertEmailAttachmentsUsecase.exec(userId, emailId, attachments);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<Void> uploadAnswerAttachments(
        Authentication authentication,
        @PathVariable("answerId") UUID answerId,
        @RequestParam(value = "attachments", required = true) List<MultipartFile> attachments) {
        Long userId = Long.valueOf(authentication.getName());

        insertAnswerAttachmentsUsecase.exec(userId, answerId, attachments);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<DownloadAttachmentOutputDTO> getEmailAttachmentDownload(
        Authentication authentication,
        @PathVariable("attachmentId") UUID attachmentId,
        @PathVariable("emailId") UUID emailId
    ) {
        try {
            Long userId = Long.valueOf(authentication.getName());
            DownloadAttachmentOutputDTO output = downloadAttachmentUsecase.exec(userId, emailId, attachmentId, false, null);

            return new ResponseEntity<>(output, HttpStatus.OK);
        } catch (Exception exception) {
            System.out.println(exception.getStackTrace());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<DownloadAttachmentOutputDTO> getAnswerAttachmentDownload(
        Authentication authentication,
        @PathVariable("attachmentId") UUID attachmentId,
        @PathVariable("emailId") UUID emailId,
        @PathVariable("answerId") UUID answerId
    ) {
        try {
            Long userId = Long.valueOf(authentication.getName());
            DownloadAttachmentOutputDTO output = downloadAttachmentUsecase.exec(userId, emailId, attachmentId, true, answerId);

            return new ResponseEntity<>(output, HttpStatus.OK);
        } catch (Exception exception) {
            System.out.println(exception.getStackTrace());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
