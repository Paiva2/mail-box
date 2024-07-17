package com.root.mailbox.domain.usecases.attachment;

import com.root.mailbox.domain.entities.*;
import com.root.mailbox.domain.exceptions.answerAttachment.AnswerAttachmentNotFoundException;
import com.root.mailbox.domain.exceptions.attachment.AttachmentNotFoundException;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.domain.exceptions.userEmail.UserEmailNotFoundException;
import com.root.mailbox.domain.utils.AwsAdapter;
import com.root.mailbox.infra.providers.*;
import com.root.mailbox.presentation.dto.attachment.DownloadAttachmentOutputDTO;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
public class DownloadAttachmentUsecase {
    @Value("${aws.attachment.bucket.name}")
    private String bucketName;

    private final UserDataProvider userDataProvider;
    private final EmailAttachmentDataProvider emailAttachmentDataProvider;
    private final UserEmailDataProvider userEmailDataProvider;
    private final AnswerAttachmentDataProvider answerAttachmentDataProvider;
    private final AwsAdapter awsAdapter;

    public DownloadAttachmentUsecase(UserDataProvider userDataProvider, EmailAttachmentDataProvider emailAttachmentDataProvider, UserEmailDataProvider userEmailDataProvider, AnswerAttachmentDataProvider answerAttachmentDataProvider, AwsAdapter awsAdapter) {
        this.userDataProvider = userDataProvider;
        this.emailAttachmentDataProvider = emailAttachmentDataProvider;
        this.userEmailDataProvider = userEmailDataProvider;
        this.answerAttachmentDataProvider = answerAttachmentDataProvider;
        this.awsAdapter = awsAdapter;
    }

    public DownloadAttachmentOutputDTO exec(Long userId, UUID emailId, UUID attachmentId, Boolean isAnswerAttachment, UUID answerId) throws IOException {
        User user = checkIfUserExists(userId);

        if (user.getDisabled()) {
            throw new UserDisabledException(user.getId());
        }

        checkIfUserEmailExists(user.getId(), emailId);

        Attachment attachment;

        if (isAnswerAttachment) {
            AnswerAttachment answerAttachment = getAnswerAttachment(answerId, attachmentId);
            attachment = answerAttachment.getAttachment();
        } else {
            EmailAttachment emailAttachment = getEmailAttachment(emailId, attachmentId);
            attachment = emailAttachment.getAttachment();
        }

        ResponseInputStream<GetObjectResponse> serviceResponse = awsAdapter.getFileOnBucket(bucketName, attachment.getUploadServiceFileName());

        String contentType = serviceResponse.response().contentType();
        String originalFileName = attachment.getFileName();
        byte[] fileContent = IOUtils.toByteArray(serviceResponse);

        return mountOutput(contentType, originalFileName, fileContent);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private void checkIfUserEmailExists(Long userId, UUID emailId) {
        Optional<UserEmail> userEmail = userEmailDataProvider.findUserEmail(userId, emailId);

        if (userEmail.isEmpty()) {
            throw new UserEmailNotFoundException(emailId.toString(), userId.toString());
        }
    }

    private EmailAttachment getEmailAttachment(UUID emailId, UUID attachmentId) {
        return emailAttachmentDataProvider.findByEmailAndAttachment(emailId, attachmentId).orElseThrow(AttachmentNotFoundException::new);
    }

    private AnswerAttachment getAnswerAttachment(UUID answerId, UUID attachmentId) {
        return answerAttachmentDataProvider.findByAnswerAndAttachment(answerId, attachmentId).orElseThrow(AnswerAttachmentNotFoundException::new);
    }

    private DownloadAttachmentOutputDTO mountOutput(String contentType, String originalFileName, byte[] content) {
        return DownloadAttachmentOutputDTO.builder()
            .originalFileName(originalFileName)
            .contentType(contentType)
            .fileContentBytes(content)
            .build();
    }
}
