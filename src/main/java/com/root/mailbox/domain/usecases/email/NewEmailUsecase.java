package com.root.mailbox.domain.usecases.email;

import com.root.mailbox.domain.entities.CarbonCopy;
import com.root.mailbox.domain.entities.Email;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.entities.UserEmail;
import com.root.mailbox.domain.exceptions.CarbonCopiesNotFoundException;
import com.root.mailbox.domain.exceptions.OpeningOrderWithCopiesException;
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
        if (newEmail.getOpeningOrders() && !newEmail.getCCopies().isEmpty()) {
            throw new OpeningOrderWithCopiesException();
        }

        User user = checkIfUserExists(userId);
        List<User> usersTo = checkIfUsersToExists(newEmail.getUsersEmails());

        if (Objects.nonNull(user.getDisabled())) {
            throw new UserDisabledException(user.getId());
        }

        Email email = createEmail(newEmail);

        if (Objects.nonNull(newEmail.getCCopies()) && !newEmail.getCCopies().isEmpty() && !newEmail.getOpeningOrders()) {
            handleCopiedUsers(newEmail.getCCopies(), email);
        }

        createUserEmail(usersTo, email);
    }

    private Email createEmail(Email email) {
        email.setDisabled(false);

        return emailDataProvider.create(email);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private void createUserEmail(List<User> usersTo, Email email) {
        List<UserEmail> userEmails = new ArrayList<>();

        for (int i = 0; i <= usersTo.size(); i++) {
            UserEmail userEmail = new UserEmail(usersTo.get(i), email, false, false);

            if (email.getOpeningOrders()) {
                userEmail.setOpeningOrder(i);
            }

            userEmails.add(userEmail);
        }

        emailDataProvider.createUsersEmails(userEmails);
    }

    private void handleCopiedUsers(List<CarbonCopy> carbonCopies, Email email) {
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

    private List<User> checkIfUsersToExists(List<UserEmail> usersEmails) {
        List<String> emails = usersEmails.stream().map(userEmail -> userEmail.getUser().getEmail()).toList();
        List<User> usersFound = userDataProvider.findAllUsersByEmail(emails);
        List<String> emailsNotFound = new ArrayList<>();

        emails.forEach(email -> {
            Optional<User> user = usersFound.stream().filter(userFound -> userFound.getEmail().equals(email)).findAny();

            if (user.isEmpty()) {
                emailsNotFound.add(email);
            }
        });

        if (!emailsNotFound.isEmpty()) {
            throw new UserNotFoundException(emailsNotFound.toString());
        }

        return usersFound;
    }
}
