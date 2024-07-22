package com.root.mailbox.domain.usecases.user;

import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.exceptions.attachment.AttachmentMaxSizeExceeded;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.domain.utils.AwsAdapter;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.presentation.dto.user.InvalidProfilePictureTypeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.UUID;

@Service
public class UploadProfilePictureUsecase {
    private final static Integer MAX_FILE_SUPPORTED_SIZE = 3 * 1024 * 1024; // 3MB

    @Value("${aws.attachment.bucket.name}")
    private String BUCKET_NAME;
    private final UserDataProvider userDataProvider;
    private final AwsAdapter awsAdapter;

    public UploadProfilePictureUsecase(UserDataProvider userDataProvider, AwsAdapter awsAdapter) {
        this.userDataProvider = userDataProvider;
        this.awsAdapter = awsAdapter;
    }

    public String exec(Long userId, MultipartFile multipartFile) {
        User user = checkIfUserExists(userId);

        if (user.getDisabled()) {
            throw new UserDisabledException(user.getId());
        }

        validateContentType(multipartFile);
        validateFileSize(multipartFile);

        String fileName = "profile_pic_".concat(UUID.randomUUID().toString());
        String profileUrl = awsAdapter.insertFileOnBucket(BUCKET_NAME, multipartFile, fileName);

        user.setProfilePicture(profileUrl);

        persistUserUpdated(user);

        return profileUrl;
    }


    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private void validateContentType(MultipartFile multipartFile) {
        String type = multipartFile.getContentType();

        if (Objects.isNull(type)) {
            throw new InvalidProfilePictureTypeException();
        } else if (!type.contains("jpg") && !type.contains("jpeg") && !type.contains("png")) {
            throw new InvalidProfilePictureTypeException();
        }
    }

    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SUPPORTED_SIZE) {
            throw new AttachmentMaxSizeExceeded(file.getOriginalFilename());
        }
    }

    private void persistUserUpdated(User user) {
        userDataProvider.create(user);
    }
}
