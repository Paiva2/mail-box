package com.root.mailbox.domain.usecases.attachment;

import com.root.mailbox.domain.entities.*;
import com.root.mailbox.domain.entities.enums.FileExtension;
import com.root.mailbox.domain.exceptions.answer.AnswerNotFoundException;
import com.root.mailbox.domain.exceptions.attachment.AttachmentMaxSizeExceeded;
import com.root.mailbox.domain.exceptions.attachment.AttachmentMediaTypeNotSupportedException;
import com.root.mailbox.domain.exceptions.attachment.EmailAlreadyHasAttachmentException;
import com.root.mailbox.domain.exceptions.attachment.EmptyAttachmentPropertyException;
import com.root.mailbox.domain.exceptions.email.EmailNotFoundException;
import com.root.mailbox.domain.exceptions.generic.ForbiddenException;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.domain.utils.AwsAdapter;
import com.root.mailbox.infra.providers.*;
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
public class InsertAnswerAttachmentsUsecase {
    @Value("${aws.attachment.bucket.name}")
    private String bucketName;

    private final static Integer MAX_FILE_SUPPORTED_SIZE = 5 * 1024 * 1024; // 5MB

    private final UserDataProvider userDataProvider;
    private final AttachmentDataProvider attachmentDataProvider;
    private final AnswerAttachmentDataProvider answerAttachmentDataProvider;
    private final AnswerDataProvider answerDataProvider;
    private final AwsAdapter awsAdapter;

    public InsertAnswerAttachmentsUsecase(UserDataProvider userDataProvider, AttachmentDataProvider attachmentDataProvider, AnswerAttachmentDataProvider answerAttachmentDataProvider, AnswerDataProvider answerDataProvider, AwsAdapter awsAdapter) {
        this.userDataProvider = userDataProvider;
        this.attachmentDataProvider = attachmentDataProvider;
        this.answerAttachmentDataProvider = answerAttachmentDataProvider;
        this.answerDataProvider = answerDataProvider;
        this.awsAdapter = awsAdapter;
    }

    @Transactional
    public List<AttachmentOutputDTO> exec(Long userId, UUID answerId, List<MultipartFile> attachments) {
        validateFiles(attachments);

        User user = checkIfUserExists(userId);

        if (user.getDisabled()) {
            throw new UserDisabledException(user.getId());
        }

        Answer answer = checkIfAnswerExists(user.getId(), answerId);

        if (answer.getDisabled()) {
            throw new EmailNotFoundException();
        }

        checkPermissions(user, answer);

        if (!answer.getAnswerAttachments().isEmpty()) {
            throw new EmailAlreadyHasAttachmentException();
        }

        List<Attachment> attachmentsList = new ArrayList<>();

        attachments.forEach(attachment -> {
            String formattedType = formatContentType(attachment.getContentType()).toUpperCase();
            String fileNameBucket = "attachments_".concat(UUID.randomUUID().toString());

            String url = awsAdapter.insertFileOnBucket(bucketName, attachment, fileNameBucket);

            attachmentsList.add(Attachment.builder()
                .user(user)
                .url(url)
                .fileName(attachment.getOriginalFilename())
                .uploadServiceFileName(fileNameBucket)
                .extension(FileExtension.valueOf(formattedType.toUpperCase()))
                .build()
            );
        });

        List<Attachment> attachmentsCreated = saveAttachments(attachmentsList);

        attachmentsCreated.forEach(attachment -> {
            AnswerAttachment answerAttachment = new AnswerAttachment(answer, attachment);
            answerAttachmentDataProvider.save(answerAttachment);
        });

        return mountOutput(attachmentsCreated);
    }

    private void validateFiles(List<MultipartFile> files) {
        List<String> supportedExtensions = Stream.of(FileExtension.values()).map(FileExtension::getExtension).toList();

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
        return contentType.split("/")[1];
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private Answer checkIfAnswerExists(Long userId, UUID answerId) {
        return answerDataProvider.findByIdAndUserId(answerId, userId).orElseThrow(AnswerNotFoundException::new);
    }

    private void checkPermissions(User user, Answer answer) {
        Long userId = user.getId();
        Long answerUserId = answer.getUser().getId();

        if (!userId.equals(answerUserId)) {
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
