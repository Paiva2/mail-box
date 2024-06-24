package com.root.mailbox.domain.usecases.email;

import com.root.mailbox.domain.entities.Email;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.exceptions.email.EmailNonDraftException;
import com.root.mailbox.domain.exceptions.email.EmailNotFoundException;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.infra.providers.EmailDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@AllArgsConstructor
@Service
public class DeleteDraftEmailUsecase {
    private final UserDataProvider userDataProvider;
    private final EmailDataProvider emailDataProvider;

    public void exec(Long userId, UUID emailId) {
        User user = checkIfUserExists(userId);

        if (user.getDisabled()) {
            throw new UserDisabledException(user.getId());
        }

        Email email = checkIfEmailExists(emailId);

        if (!email.getEmailStatus().equals(Email.EmailStatus.DRAFT)) {
            throw new EmailNonDraftException();
        }

        if (email.getDisabled()) {
            throw new EmailNotFoundException();
        }

        checkPermissions(email, user);

        deleteDraftEmail(emailId);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private Email checkIfEmailExists(UUID emailId) {
        return emailDataProvider.findById(emailId).orElseThrow(EmailNotFoundException::new);
    }

    private void checkPermissions(Email email, User user) {
        Long userId = user.getId();
        Long emailUserId = email.getUser().getId();

        if (!userId.equals(emailUserId)) {
            throw new EmailNotFoundException();
        }
    }

    private void deleteDraftEmail(UUID emailId) {
        emailDataProvider.deleteById(emailId);
    }
}
