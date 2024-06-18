package com.root.mailbox.domain.usecases.email;

import com.root.mailbox.domain.entities.*;
import com.root.mailbox.domain.exceptions.email.RepeatedUsersToOrCopyException;
import com.root.mailbox.domain.exceptions.email.UserToInCopyListException;
import com.root.mailbox.domain.exceptions.openingOrder.OpeningOrderWithCopiesException;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.infra.providers.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class NewEmailUsecase {
    private final UserDataProvider userDataProvider;
    private final EmailDataProvider emailDataProvider;
    private final EmailOpeningOrderDataProvider emailOpeningOrderDataProvider;
    private final UserEmailDataProvider userEmailDataProvider;

    @Transactional
    public void exec(Email newEmail, Long userId) {
        List<UserEmail> copies = newEmail.getUsersEmails().stream().filter(user -> user.getEmailType().equals(UserEmail.EmailType.IN_COPY)).toList();

        if (newEmail.getOpeningOrders() && !copies.isEmpty()) {
            throw new OpeningOrderWithCopiesException();
        } else if (newEmail.getOpeningOrders() && newEmail.getUsersEmails().size() == 1) {
            newEmail.setOpeningOrders(false);
        }

        User user = checkIfUserExists(userId);
        newEmail.setUser(user);

        List<User> usersTo = checkIfUsersToExists(newEmail.getUsersEmails());

        checkUsersToIsOnCopy(newEmail);
        checkRepeatedUsersToAndCopies(newEmail);

        if (user.getDisabled()) {
            throw new UserDisabledException(user.getId());
        }

        Email email = createEmail(newEmail);

        createUsersEmails(usersTo, email);
    }

    private Email createEmail(Email email) {
        email.setDisabled(false);

        return emailDataProvider.create(email);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private void createUsersEmails(List<User> usersTo, Email email) {
        List<UserEmail> userEmails = email.getUsersEmails();
        List<EmailOpeningOrder> emailOpeningOrders = new ArrayList<>();
        List<UserEmail> userEmailsToCreate = new ArrayList<>();

        for (int i = 0; i <= usersTo.size() - 1; i++) {
            int index = i;

            Optional<UserEmail> userEmail = userEmails.stream().filter(ue -> ue.getUser().equals(usersTo.get(index))).findAny();

            if (userEmail.isEmpty()) {
                throw new RuntimeException("Unexpected Error while creating UsersEmails...");
            }

            userEmail.get().setOpened(false);
            userEmail.get().setUser(usersTo.get(i));
            userEmail.get().setEmail(email);

            if (email.getOpeningOrders()) {
                if (userEmail.get().getEmailType().equals(UserEmail.EmailType.RECEIVED)) {
                    Optional<EmailOpeningOrder> firstOrderAlreadyCreated = emailOpeningOrders.stream().findFirst();

                    if (firstOrderAlreadyCreated.isEmpty()) {
                        emailOpeningOrders.add(mountOpeningOrder(usersTo.get(i), email, i + 1));
                        userEmailsToCreate.add(userEmail.get());
                    }
                }
            } else if (!userEmail.get().getEmailType().equals(UserEmail.EmailType.SENT)) {
                userEmailsToCreate.add(userEmail.get());
            }
        }

        if (!emailOpeningOrders.isEmpty()) {
            emailOpeningOrderDataProvider.createEmailOrders(emailOpeningOrders);
        }

        UserEmail userEmailToOwner = new UserEmail(email.getUser(), email, false, false, UserEmail.EmailType.SENT);
        userEmailToOwner.setOpened(true);

        userEmails.add(userEmailToOwner);

        userEmailDataProvider.createUsersEmails(userEmailsToCreate);
    }

    private List<User> checkIfUsersToExists(List<UserEmail> usersEmails) {
        List<String> emails = usersEmails.stream().map(userEmail -> userEmail.getUser().getEmail()).toList();
        List<User> usersFound = new ArrayList<>();
        List<String> emailsNotFound = new ArrayList<>();

        emails.forEach(email -> {
            Optional<User> user = userDataProvider.findUserByEmail(email);

            if (user.isEmpty()) {
                emailsNotFound.add(email);
            } else {
                usersFound.add(user.get());
            }
        });

        if (!emailsNotFound.isEmpty()) {
            throw new UserNotFoundException(emailsNotFound.toString());
        }

        return usersFound;
    }

    private void checkUsersToIsOnCopy(Email newEmail) {
        List<String> usersOnCopy = newEmail.getUsersEmails().stream().filter(user -> user.getEmailType().equals(UserEmail.EmailType.IN_COPY)).map(user -> user.getUser().getEmail()).toList();
        List<String> usersTo = newEmail.getUsersEmails().stream().filter(user -> user.getEmailType().equals(UserEmail.EmailType.RECEIVED)).map(user -> user.getUser().getEmail()).toList();

        usersOnCopy.forEach(userOnCopy -> {
            Boolean doesUserOnCopyIsOnUsersTo = usersTo.contains(userOnCopy);

            if (doesUserOnCopyIsOnUsersTo) {
                throw new UserToInCopyListException();
            }
        });

        usersTo.forEach(userTo -> {
            Boolean doesUserToIsOnCopy = usersOnCopy.contains(userTo);

            if (doesUserToIsOnCopy) {
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
