package com.root.mailbox.domain.usecases.email;

import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.entities.UserEmail;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.domain.exceptions.userEmail.UserEmailNotFoundException;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.infra.providers.UserEmailDataProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
public class HandleUserEmailFlagUsecase {
    private final UserDataProvider userDataProvider;
    private final UserEmailDataProvider userEmailDataProvider;

    public void exec(Long userId, UUID emailId, UserEmail.EmailFlag flag) {
        User user = checkIfUserExists(userId);

        if (user.getDisabled()) {
            throw new UserDisabledException(user.getId());
        }

        UserEmail userEmail = checkIfUserEmailExists(user.getId(), emailId);

        if (userEmail.getDisabled() || Objects.nonNull(userEmail.getDeletedAt())) {
            throw new UserEmailNotFoundException(emailId.toString(), user.getId().toString());
        }

        if (Objects.isNull(flag)) {
            userEmail.setEmailFlag(UserEmail.EmailFlag.INBOX);
        } else {
            userEmail.setEmailFlag(flag);
        }

        persistUserEmail(userEmail);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private UserEmail checkIfUserEmailExists(Long userId, UUID emailId) {
        return userEmailDataProvider.findUserEmail(userId, emailId).orElseThrow(() -> new UserEmailNotFoundException(emailId.toString(), userId.toString()));
    }

    private void persistUserEmail(UserEmail userEmail) {
        userEmailDataProvider.save(userEmail);
    }
}
