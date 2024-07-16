package com.root.mailbox.domain.usecases.attachment;

import com.root.mailbox.domain.entities.Attachment;
import com.root.mailbox.domain.entities.EmailAttachment;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.entities.UserEmail;
import com.root.mailbox.domain.exceptions.attachment.AttachmentNotFoundException;
import com.root.mailbox.domain.exceptions.email.EmailNotFoundException;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.domain.exceptions.userEmail.UserEmailNotFoundException;
import com.root.mailbox.domain.utils.AwsAdapter;
import com.root.mailbox.infra.providers.AttachmentDataProvider;
import com.root.mailbox.infra.providers.EmailAttachmentDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.infra.providers.UserEmailDataProvider;
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
    private final AwsAdapter awsAdapter;

    public DownloadAttachmentUsecase(UserDataProvider userDataProvider, EmailAttachmentDataProvider emailAttachmentDataProvider, UserEmailDataProvider userEmailDataProvider, AwsAdapter awsAdapter) {
        this.userDataProvider = userDataProvider;
        this.emailAttachmentDataProvider = emailAttachmentDataProvider;
        this.userEmailDataProvider = userEmailDataProvider;
        this.awsAdapter = awsAdapter;
    }

    public DownloadAttachmentOutputDTO exec(Long userId, UUID emailId, UUID attachmentId) throws IOException {
        User user = checkIfUserExists(userId);

        if (user.getDisabled()) {
            throw new UserDisabledException(user.getId());
        }

        checkIfUserEmailExists(user.getId(), emailId);

        EmailAttachment emailAttachment = getEmailAttachment(emailId, attachmentId);
        Attachment attachment = emailAttachment.getAttachment();

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

    private DownloadAttachmentOutputDTO mountOutput(String contentType, String originalFileName, byte[] content) {
        return DownloadAttachmentOutputDTO.builder()
            .originalFileName(originalFileName)
            .contentType(contentType)
            .fileContentBytes(content)
            .build();
    }
}
