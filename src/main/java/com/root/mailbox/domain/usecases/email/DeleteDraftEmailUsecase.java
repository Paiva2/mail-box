package com.root.mailbox.domain.usecases.email;

import com.root.mailbox.domain.entities.Email;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.entities.UserEmail;
import com.root.mailbox.domain.exceptions.email.EmailNonDraftException;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.domain.exceptions.userEmail.UserEmailNotFoundException;
import com.root.mailbox.domain.usecases.trashBin.SendUserEmailToTrashUsecase;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.infra.providers.UserEmailDataProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@AllArgsConstructor
@Service
public class DeleteDraftEmailUsecase {
    private final UserDataProvider userDataProvider;
    private final SendUserEmailToTrashUsecase sendUserEmailToTrashUsecase;
    private final UserEmailDataProvider userEmailDataProvider;

    public void exec(Long userId, UUID emailId) {
        User user = checkIfUserExists(userId);

        if (user.getDisabled()) {
            throw new UserDisabledException(user.getId());
        }

        UserEmail userEmail = checkIfUserEmailExists(user.getId(), emailId);

        if (!userEmail.getEmail().getEmailStatus().equals(Email.EmailStatus.DRAFT)) {
            throw new EmailNonDraftException();
        }

        if (userEmail.getDisabled()) {
            throw new UserEmailNotFoundException(emailId.toString(), user.getId().toString());
        }

        deleteDraftEmail(user.getId(), emailId);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private UserEmail checkIfUserEmailExists(Long userId, UUID emailId) {
        return userEmailDataProvider.findUserEmail(userId, emailId).orElseThrow(() -> new UserEmailNotFoundException(emailId.toString(), userId.toString()));
    }

    private void deleteDraftEmail(Long userId, UUID emailId) {
        sendUserEmailToTrashUsecase.exec(userId, emailId);
    }
}
