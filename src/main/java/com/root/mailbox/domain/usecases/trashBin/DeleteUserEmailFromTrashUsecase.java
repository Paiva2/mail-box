package com.root.mailbox.domain.usecases.trashBin;

import com.root.mailbox.domain.entities.Email;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.entities.UserEmail;
import com.root.mailbox.domain.exceptions.email.EmailNotFoundException;
import com.root.mailbox.domain.exceptions.trashBinUserEmail.UserEmailNotOnTrashException;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.domain.exceptions.userEmail.UserEmailNotFoundException;
import com.root.mailbox.infra.providers.EmailDataProvider;
import com.root.mailbox.infra.providers.TrashBinUserEmailDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.infra.providers.UserEmailDataProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Service
public class DeleteUserEmailFromTrashUsecase {
    private final UserDataProvider userDataProvider;
    private final UserEmailDataProvider userEmailDataProvider;
    private final EmailDataProvider emailDataProvider;
    private final TrashBinUserEmailDataProvider trashBinUserEmailDataProvider;

    @Transactional
    public void exec(Long userId, UUID emailId) {
        User user = checkIfUserExists(userId);

        if (user.getDisabled()) {
            throw new UserDisabledException(user.getId());
        }

        Email email = checkIfEmailExists(emailId);

        UserEmail userEmail = checkIfUserEmailExists(user.getId(), email.getId());

        if (Objects.nonNull(userEmail.getDeletedAt())) {
            throw new UserEmailNotFoundException(email.getId().toString(), user.getId().toString());
        }

        checkIfUserEmailIsOnTrash(userEmail, user.getTrashBin().getId());
        setUserEmailDeleted(userEmail);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private Email checkIfEmailExists(UUID emailId) {
        return emailDataProvider.findById(emailId).orElseThrow(EmailNotFoundException::new);
    }

    private UserEmail checkIfUserEmailExists(Long userId, UUID emailId) {
        return userEmailDataProvider.findUserEmail(userId, emailId).orElseThrow(() -> new UserEmailNotFoundException(emailId.toString(), userId.toString()));
    }

    private void checkIfUserEmailIsOnTrash(UserEmail userEmail, UUID trashId) {
        Long userId = userEmail.getUser().getId();
        UUID emailId = userEmail.getEmail().getId();

        trashBinUserEmailDataProvider.findByUserTrashAndEmailId(userId, emailId, trashId).orElseThrow(UserEmailNotOnTrashException::new);

        deleteUserEmailFromTrash(userId, emailId, trashId);
    }

    private void deleteUserEmailFromTrash(Long userId, UUID emailId, UUID trashId) {
        trashBinUserEmailDataProvider.delete(userId, emailId, trashId);
    }

    private void setUserEmailDeleted(UserEmail userEmail) {
        userEmail.setDisabled(true);
        userEmail.setDeletedAt(new Date());

        userEmailDataProvider.save(userEmail);
    }
}
