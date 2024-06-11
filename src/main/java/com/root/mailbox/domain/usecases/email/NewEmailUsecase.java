package com.root.mailbox.domain.usecases.email;

import com.root.mailbox.domain.entities.*;
import com.root.mailbox.domain.exceptions.*;
import com.root.mailbox.infra.providers.CarbonCopyDataProvider;
import com.root.mailbox.infra.providers.EmailDataProvider;
import com.root.mailbox.infra.providers.EmailOpeningOrderDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class NewEmailUsecase {
    private final UserDataProvider userDataProvider;
    private final EmailDataProvider emailDataProvider;
    private final CarbonCopyDataProvider carbonCopyDataProvider;
    private final EmailOpeningOrderDataProvider emailOpeningOrderDataProvider;

    @Transactional
    public void exec(Email newEmail, Long userId) {
        if (newEmail.getOpeningOrders() && !newEmail.getCCopies().isEmpty()) {
            throw new OpeningOrderWithCopiesException();
        } else if (newEmail.getOpeningOrders() && newEmail.getUsersEmails().size() < 1) {
            newEmail.setOpeningOrders(false);
        }

        User user = checkIfUserExists(userId);
        List<User> usersTo = checkIfUsersToExists(newEmail.getUsersEmails());

        checkUsersToIsOnCopy(newEmail);
        checkRepeatedUsersToAndCopies(newEmail);

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
        List<EmailOpeningOrder> emailOpeningOrders = new ArrayList<>();

        for (int i = 0; i <= usersTo.size() - 1; i++) {
            UserEmail userEmail = new UserEmail(usersTo.get(i), email, false, false);
            userEmail.setOpened(false);

            if (email.getOpeningOrders()) {
                emailOpeningOrders.add(mountOpeningOrder(usersTo.get(i), email, i + 1));

                if (userEmails.isEmpty()) {
                    userEmails.add(userEmail);
                }
            } else {
                userEmails.add(userEmail);
            }
        }

        if (!emailOpeningOrders.isEmpty()) {
            emailOpeningOrderDataProvider.createEmailOrders(emailOpeningOrders);
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

    private void checkUsersToIsOnCopy(Email newEmail) {
        newEmail.getUsersEmails().forEach(userTo -> {
            Optional<CarbonCopy> copiedUser = newEmail.getCCopies().stream().filter(copy -> copy.getUser().getEmail().equals(userTo.getUser().getEmail())).findFirst();

            if (copiedUser.isPresent()) {
                throw new UserToInCopyListException();
            }
        });

        newEmail.getCCopies().forEach(copy -> {
            Optional<UserEmail> userTo = newEmail.getUsersEmails().stream().filter(user -> user.getUser().getEmail().equals(copy.getUser().getEmail())).findFirst();

            if (userTo.isPresent()) {
                throw new UserToInCopyListException();
            }
        });
    }

    private void checkRepeatedUsersToAndCopies(Email newEmail) {
        List<String> emailsTo = newEmail.getUsersEmails().stream().map(user -> user.getUser().getEmail()).toList();
        HashSet<String> userEmailNonRepeated = new HashSet<>(emailsTo);

        if (userEmailNonRepeated.size() != newEmail.getUsersEmails().size()) {
            throw new RepeatedUsersToOrCopyException();
        }

        List<String> emailsCopy = newEmail.getCCopies().stream().map(copy -> copy.getUser().getEmail()).toList();
        HashSet<String> usersCopiedNonRepeated = new HashSet<>(emailsCopy);

        if (usersCopiedNonRepeated.size() != newEmail.getCCopies().size()) {
            throw new RepeatedUsersToOrCopyException();
        }
    }

    private EmailOpeningOrder mountOpeningOrder(User user, Email email, Integer order) {
        return EmailOpeningOrder.builder()
            .order(order)
            .email(email)
            .user(user)
            .status(EmailOpeningOrder.OpeningStatus.NOT_OPENED)
            .build();
    }
}
