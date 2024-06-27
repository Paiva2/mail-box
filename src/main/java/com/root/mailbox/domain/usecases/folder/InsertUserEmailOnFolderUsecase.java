package com.root.mailbox.domain.usecases.folder;

import com.root.mailbox.domain.entities.Folder;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.entities.UserEmail;
import com.root.mailbox.domain.exceptions.folder.FolderNotFoundException;
import com.root.mailbox.domain.exceptions.generic.ForbiddenException;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.domain.exceptions.userEmail.UserEmailNotFoundException;
import com.root.mailbox.infra.providers.FolderDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.infra.providers.UserEmailDataProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Service
public class InsertUserEmailOnFolderUsecase {
    private final UserDataProvider userDataProvider;
    private final UserEmailDataProvider userEmailDataProvider;
    private final FolderDataProvider folderDataProvider;

    public void exec(Long userId, UUID emailId, Long folderId) {
        User user = checkIfUserExists(userId);

        if (user.getDisabled()) {
            throw new UserDisabledException(user.getId());
        }

        UserEmail userEmail = checkIfUserEmailExists(user.getId(), emailId);

        if (Objects.nonNull(userEmail.getDeletedAt())) {
            throw new UserEmailNotFoundException(emailId.toString(), user.getId().toString());
        }

        Folder folder = checkIfFolderExists(folderId);

        if (folder.getDisabled()) {
            throw new FolderNotFoundException(folder.getId());
        }

        checkPermissions(folder.getUser().getId(), user.getId());

        setUserEmailFolder(userEmail, folder);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private UserEmail checkIfUserEmailExists(Long userId, UUID emailId) {
        return userEmailDataProvider.findUserEmail(userId, emailId).orElseThrow(() -> new UserEmailNotFoundException(emailId.toString(), userId.toString()));
    }

    private Folder checkIfFolderExists(Long folderId) {
        return folderDataProvider.findById(folderId).orElseThrow(() -> new FolderNotFoundException(folderId));
    }

    private void checkPermissions(Long folderUserId, Long userId) {
        if (!folderUserId.equals(userId)) {
            throw new ForbiddenException();
        }
    }

    private void setUserEmailFolder(UserEmail userEmail, Folder folder) {
        userEmail.setFolder(folder);

        persistUserEmail(userEmail);
    }

    private void persistUserEmail(UserEmail userEmail) {
        userEmailDataProvider.save(userEmail);
    }
}
