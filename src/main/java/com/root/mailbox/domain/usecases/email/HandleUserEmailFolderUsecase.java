package com.root.mailbox.domain.usecases.email;

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

@Service
@AllArgsConstructor
public class HandleUserEmailFolderUsecase {
    private final UserDataProvider userDataProvider;
    private final UserEmailDataProvider userEmailDataProvider;
    private final FolderDataProvider folderDataProvider;

    public void exec(Long userId, UUID emailId, Long newFolderId) {
        User user = checkIfUserExists(userId);

        if (user.getDisabled()) {
            throw new UserDisabledException(user.getId());
        }

        UserEmail userEmail = checkIfUserEmailExists(user.getId(), emailId);

        if (userEmail.getDisabled() || Objects.nonNull(userEmail.getDeletedAt())) {
            throw new UserEmailNotFoundException(emailId.toString(), user.getId().toString());
        }

        if (Objects.nonNull(newFolderId)) {
            Folder folder = checkIfFolderExists(newFolderId);

            if (folder.getDisabled() || Objects.nonNull(folder.getDisabledAt())) {
                throw new FolderNotFoundException(folder.getId());
            }

            handlePermission(folder, user);

            userEmail.setFolder(folder);
        } else {
            userEmail.setFolder(null);
        }

        persistUserEmail(userEmail);
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

    private void handlePermission(Folder folder, User user) {
        Long userId = user.getId();
        Long folderUserId = folder.getUser().getId();

        if (!folderUserId.equals(userId)) {
            throw new ForbiddenException();
        }
    }

    private void persistUserEmail(UserEmail userEmail) {
        userEmailDataProvider.save(userEmail);
    }
}
