package com.root.mailbox.presentation.controllers.attachment;

import com.root.mailbox.domain.usecases.attachment.DownloadAttachmentUsecase;
import com.root.mailbox.domain.usecases.attachment.InsertAttachmentsUsecase;
import com.root.mailbox.presentation.dto.attachment.DownloadAttachmentOutputDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class AttachmentControllerImpl implements AttachmentController {
    private final InsertAttachmentsUsecase insertAttachmentsUsecase;
    private final DownloadAttachmentUsecase downloadAttachmentUsecase;

    @Override
    public ResponseEntity<Void> uploadAttachments(
        Authentication authentication,
        @PathVariable("emailId") UUID emailId,
        @RequestParam(value = "attachments", required = true) List<MultipartFile> attachments) {
        Long userId = Long.valueOf(authentication.getName());

        insertAttachmentsUsecase.exec(userId, emailId, attachments);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<byte[]> getAttachmentDownload(
        Authentication authentication,
        @PathVariable("attachmentId") UUID attachmentId,
        @PathVariable("emailId") UUID emailId
    ) {
        try {
            Long userId = Long.valueOf(authentication.getName());
            DownloadAttachmentOutputDTO output = downloadAttachmentUsecase.exec(userId, emailId, attachmentId);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("charset", "utf-8");
            responseHeaders.setContentType(MediaType.valueOf(output.getContentType()));
            responseHeaders.setContentLength(output.getFileContent().length);
            responseHeaders.set("Content-disposition", "attachment; filename=" + output.getOriginalFileName());

            return new ResponseEntity<>(output.getFileContent(), responseHeaders, HttpStatus.OK);
        } catch (Exception exception) {
            System.out.println(exception.getStackTrace());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
