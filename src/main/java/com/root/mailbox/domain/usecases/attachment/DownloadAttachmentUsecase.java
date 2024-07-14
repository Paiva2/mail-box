package com.root.mailbox.domain.usecases.attachment;

import com.root.mailbox.domain.entities.Attachment;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.entities.UserEmail;
import com.root.mailbox.domain.exceptions.attachment.AttachmentNotFoundException;
import com.root.mailbox.domain.exceptions.email.EmailNotFoundException;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.domain.exceptions.userEmail.UserEmailNotFoundException;
import com.root.mailbox.domain.utils.AwsAdapter;
import com.root.mailbox.infra.providers.AttachmentDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.infra.providers.UserEmailDataProvider;
import com.root.mailbox.presentation.dto.attachment.DownloadAttachmentOutputDTO;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.util.UUID;

@Service
public class DownloadAttachmentUsecase {
    @Value("${aws.attachment.bucket.name}")
    private String bucketName;

    private final UserDataProvider userDataProvider;
    private final UserEmailDataProvider userEmailDataProvider;
    private final AttachmentDataProvider attachmentDataProvider;
    private final AwsAdapter awsAdapter;

    public DownloadAttachmentUsecase(UserDataProvider userDataProvider, UserEmailDataProvider userEmailDataProvider, AttachmentDataProvider attachmentDataProvider, AwsAdapter awsAdapter) {
        this.userDataProvider = userDataProvider;
        this.userEmailDataProvider = userEmailDataProvider;
        this.attachmentDataProvider = attachmentDataProvider;
        this.awsAdapter = awsAdapter;
    }

    public DownloadAttachmentOutputDTO exec(Long userId, UUID emailId, UUID attachmentId) throws IOException {
        User user = checkIfUserExists(userId);

        if (user.getDisabled()) {
            throw new UserDisabledException(user.getId());
        }

        UserEmail userEmail = checkIfUserEmailExists(emailId, user.getId());

        if (userEmail.getDisabled()) {
            throw new EmailNotFoundException();
        }

        Attachment attachment = checkIfAttachmentExists(attachmentId);

        checkAttachmentAndEmail(attachment, userEmail);

        ResponseInputStream<GetObjectResponse> serviceResponse = awsAdapter.getFileOnBucket(bucketName, attachment.getUploadServiceFileName());

        String contentType = serviceResponse.response().contentType();
        String originalFileName = attachment.getFileName();
        byte[] fileContent = IOUtils.toByteArray(serviceResponse);
        String fileDisposition = serviceResponse.response().contentDisposition();

        return mountOutput(contentType, originalFileName, fileContent, fileDisposition);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private UserEmail checkIfUserEmailExists(UUID emailId, Long userId) {
        return userEmailDataProvider.findUserEmail(userId, emailId).orElseThrow(() -> new UserEmailNotFoundException(emailId.toString(), userId.toString()));
    }

    private Attachment checkIfAttachmentExists(UUID attachmentId) {
        return attachmentDataProvider.findById(attachmentId).orElseThrow(AttachmentNotFoundException::new);
    }

    private void checkAttachmentAndEmail(Attachment attachment, UserEmail userEmail) {
        if (!attachment.getEmail().getId().equals(userEmail.getEmail().getId())) {
            throw new AttachmentNotFoundException();
        }
    }

    private DownloadAttachmentOutputDTO mountOutput(String contentType, String originalFileName, byte[] content, String fileDisposition) {
        return DownloadAttachmentOutputDTO.builder()
            .originalFileName(originalFileName)
            .contentType(contentType)
            .fileContentBytes(content)
            .fileDisposition(fileDisposition)
            .build();
    }
}
