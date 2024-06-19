package com.root.mailbox.domain.usecases.trashBin;

import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.entities.UserEmail;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.domain.exceptions.userEmail.UserEmailNotFoundException;
import com.root.mailbox.domain.exceptions.userEmail.UserEmailNotFoundOnTrashException;
import com.root.mailbox.infra.providers.TrashBinUserEmailDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.infra.providers.UserEmailDataProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Service
public class RecoverEmailFromTrashUsecase {
    private final UserDataProvider userDataProvider;
    private final UserEmailDataProvider userEmailDataProvider;
    private final TrashBinUserEmailDataProvider trashBinUserEmailDataProvider;

    @Transactional
    public void exec(Long userId, UUID emailId) {
        User user = checkIfUserExists(userId);

        if (user.getDisabled()) {
            throw new UserDisabledException(user.getId());
        }

        UserEmail userEmail = getUserEmail(user.getId(), emailId);

        if (Objects.nonNull(userEmail.getDeletedAt())) {
            throw new UserEmailNotFoundException(emailId.toString(), user.getId().toString());
        } else if (!userEmail.getDisabled()) {
            throw new UserEmailNotFoundOnTrashException();
        }

        checkIfUserEmailIsOnTrash(userEmail, user.getTrashBin().getId());
        enableUserEmail(userEmail);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private UserEmail getUserEmail(Long userId, UUID emailId) {
        return userEmailDataProvider.findUserEmail(userId, emailId).orElseThrow(() -> new UserEmailNotFoundException(emailId.toString(), userId.toString()));
    }

    private void checkIfUserEmailIsOnTrash(UserEmail userEmail, UUID trashId) {
        Long userId = userEmail.getUser().getId();
        UUID emailId = userEmail.getEmail().getId();

        trashBinUserEmailDataProvider.findByUserTrashAndEmailId(userId, emailId, trashId).orElseThrow(UserEmailNotFoundOnTrashException::new);

        removeUserEmailFromTrash(userId, emailId, trashId);
    }

    private void removeUserEmailFromTrash(Long userId, UUID emailId, UUID trashId) {
        trashBinUserEmailDataProvider.delete(userId, emailId, trashId);
    }

    private void enableUserEmail(UserEmail userEmail) {
        userEmail.setDisabled(false);

        userEmailDataProvider.save(userEmail);
    }
}
