package com.root.mailbox.domain.usecases.attachment;

import com.root.mailbox.domain.entities.Attachment;
import com.root.mailbox.domain.entities.Email;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.exceptions.attachment.AttachmentMaxSizeExceeded;
import com.root.mailbox.domain.exceptions.attachment.AttachmentMediaTypeNotSupportedException;
import com.root.mailbox.domain.exceptions.attachment.EmailAlreadyHasAttachmentException;
import com.root.mailbox.domain.exceptions.attachment.EmptyAttachmentPropertyException;
import com.root.mailbox.domain.exceptions.email.EmailNotFoundException;
import com.root.mailbox.domain.exceptions.generic.ForbiddenException;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.domain.utils.AwsAdapter;
import com.root.mailbox.infra.providers.AttachmentDataProvider;
import com.root.mailbox.infra.providers.EmailDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.presentation.dto.attachment.AttachmentOutputDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class InsertAttachmentsUsecase {
    @Value("${aws.attachment.bucket.name}")
    private String bucketName;

    private final static Integer MAX_FILE_SUPPORTED_SIZE = 5 * 1024 * 1024; // 5MB

    private final UserDataProvider userDataProvider;
    private final EmailDataProvider emailDataProvider;
    private final AttachmentDataProvider attachmentDataProvider;
    private final AwsAdapter awsAdapter;

    public InsertAttachmentsUsecase(UserDataProvider userDataProvider, EmailDataProvider emailDataProvider, AttachmentDataProvider attachmentDataProvider, AwsAdapter awsAdapter) {
        this.userDataProvider = userDataProvider;
        this.emailDataProvider = emailDataProvider;
        this.attachmentDataProvider = attachmentDataProvider;
        this.awsAdapter = awsAdapter;
    }

    @Transactional
    public List<AttachmentOutputDTO> exec(Long userId, UUID emailId, List<MultipartFile> attachments) {
        validateFiles(attachments);

        User user = checkIfUserExists(userId);

        if (user.getDisabled()) {
            throw new UserDisabledException(user.getId());
        }

        Email email = checkIfEmailExists(emailId, user.getId());

        if (email.getDisabled()) {
            throw new EmailNotFoundException();
        }

        checkPermissions(user, email);

        if (!email.getAttachments().isEmpty()) {
            throw new EmailAlreadyHasAttachmentException();
        }

        List<Attachment> attachmentsList = new ArrayList<>();

        attachments.forEach(attachment -> {
            String formattedType = formatContentType(attachment.getContentType()).toUpperCase();
            String fileNameBucket = "attachments_".concat(UUID.randomUUID().toString());

            String url = awsAdapter.insertFileOnBucket(bucketName, attachment, fileNameBucket);

            attachmentsList.add(Attachment.builder()
                .email(email)
                .url(url)
                .fileName(attachment.getOriginalFilename())
                .uploadServiceFileName(fileNameBucket)
                .extension(Attachment.FileExtension.valueOf(formattedType.toUpperCase()))
                .build()
            );
        });

        List<Attachment> attachmentsCreated = saveAttachments(attachmentsList);

        return mountOutput(attachmentsCreated);
    }

    private void validateFiles(List<MultipartFile> files) {
        List<String> supportedExtensions = Stream.of(Attachment.FileExtension.values()).map(Attachment.FileExtension::getExtension).toList();

        files.forEach(file -> {
            if (Objects.isNull(file.getContentType())) {
                throw new EmptyAttachmentPropertyException("Content Type");
            }

            if (Objects.isNull(file.getOriginalFilename())) {
                throw new EmptyAttachmentPropertyException("File name");
            }

            String formattedType = formatContentType(file.getContentType()).toUpperCase();

            if (file.getSize() > MAX_FILE_SUPPORTED_SIZE) {
                throw new AttachmentMaxSizeExceeded(file.getOriginalFilename());
            } else if (!supportedExtensions.contains(formattedType)) {
                throw new AttachmentMediaTypeNotSupportedException(file.getOriginalFilename());
            }
        });
    }

    private String formatContentType(String contentType) {
        return contentType.replace("application/", "");
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private Email checkIfEmailExists(UUID emailId, Long userId) {
        return emailDataProvider.findByIdAndUserId(emailId, userId).orElseThrow(EmailNotFoundException::new);
    }

    private void checkPermissions(User user, Email email) {
        Long userId = user.getId();
        Long userEmailId = email.getUser().getId();

        if (!userId.equals(userEmailId)) {
            throw new ForbiddenException();
        }
    }

    private List<Attachment> saveAttachments(List<Attachment> attachments) {
        return attachmentDataProvider.saveAll(attachments);
    }

    private List<AttachmentOutputDTO> mountOutput(List<Attachment> attachments) {
        return attachments.stream().map(attachment -> AttachmentOutputDTO.builder()
                .id(attachment.getId())
                .url(attachment.getUrl())
                .fileName(attachment.getFileName())
                .extension(attachment.getExtension())
                .createdAt(attachment.getCreatedAt())
                .build())
            .toList();
    }
}
