package com.root.mailbox.domain.usecases.email;

import com.root.mailbox.domain.entities.CarbonCopy;
import com.root.mailbox.domain.entities.Email;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.entities.UserEmail;
import com.root.mailbox.domain.exceptions.CarbonCopiesNotFoundException;
import com.root.mailbox.domain.exceptions.UserDisabledException;
import com.root.mailbox.domain.exceptions.UserNotFoundException;
import com.root.mailbox.infra.providers.CarbonCopyDataProvider;
import com.root.mailbox.infra.providers.EmailDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class NewEmailUsecase {
    private UserDataProvider userDataProvider;
    private EmailDataProvider emailDataProvider;
    private CarbonCopyDataProvider carbonCopyDataProvider;

    @Transactional
    public void exec(Email newEmail, Long userId) {
        User user = checkIfUserExists(userId);
        User userTo = checkIfUserToExists(newEmail.getUserTo().getEmail());
        newEmail.setUserTo(userTo);

        if (Objects.nonNull(user.getDisabled())) {
            throw new UserDisabledException(user.getId());
        }

        Email email = createEmail(newEmail);

        if (Objects.nonNull(newEmail.getCCopies()) && !newEmail.getCCopies().isEmpty()) {
            handleCopiedUsers(newEmail.getCCopies(), email, user);
        }

        createUserEmail(user, email);
    }

    private Email createEmail(Email email) {
        email.setDisabled(false);
        email.setOpened(false);
        email.setIsSpam(false);

        return emailDataProvider.create(email);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private void createUserEmail(User user, Email email) {
        UserEmail userEmail = new UserEmail(user, email);

        emailDataProvider.createUserEmail(userEmail);
    }

    private void handleCopiedUsers(List<CarbonCopy> carbonCopies, Email email, User user) {
        List<String> copiesEmailError = new ArrayList<>();

        carbonCopies.forEach(copy -> {
            Optional<User> copiedUser = userDataProvider.findUserByEmail(copy.getUser().getEmail());

            if (copiedUser.isEmpty()) {
                copiesEmailError.add(copy.getUser().getEmail());
            } else {
                copy.setUser(copiedUser.get());
                copy.setEmail(email);
                copy.setOpened(false);
                copy.setIsSpam(false);
            }
        });

        if (!copiesEmailError.isEmpty()) {
            throw new CarbonCopiesNotFoundException(copiesEmailError.toString());
        } else {
            createEmailCarbonCopies(carbonCopies);
        }
    }

    private void createEmailCarbonCopies(List<CarbonCopy> carbonCopies) {
        carbonCopyDataProvider.saveAllCopies(carbonCopies);
    }

    private User checkIfUserToExists(String email) {
        return userDataProvider.findUserByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }
}
