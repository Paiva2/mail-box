package com.root.mailbox.domain.usecases.email;

import com.root.mailbox.domain.entities.*;
import com.root.mailbox.domain.exceptions.email.RepeatedUsersToOrCopyException;
import com.root.mailbox.domain.exceptions.email.SendingEmailToHimselfUsecase;
import com.root.mailbox.domain.exceptions.email.UserToInCopyListException;
import com.root.mailbox.domain.exceptions.openingOrder.OpeningOrderWithCopiesException;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.domain.exceptions.userEmail.UserEmailNotFoundException;
import com.root.mailbox.infra.providers.*;
import com.root.mailbox.presentation.dto.email.EmailOutputDTO;
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
    public EmailOutputDTO exec(Email newEmail, Long userId) {
        List<UserEmail> copies = newEmail.getUsersEmails().stream().filter(user -> user.getEmailType().equals(UserEmail.EmailType.IN_COPY)).toList();

        if (newEmail.getOpeningOrders() && !copies.isEmpty()) {
            throw new OpeningOrderWithCopiesException();
        } else if (newEmail.getOpeningOrders() && newEmail.getUsersEmails().size() == 1) {
            newEmail.setOpeningOrders(false);
        }

        User user = checkIfUserExists(userId);

        if (user.getDisabled()) {
            throw new UserDisabledException(user.getId());
        }

        newEmail.setUser(user);

        List<UserEmail> usersEmails = newEmail.getUsersEmails();

        List<User> usersTo = checkIfUsersToExists(usersEmails);
        checkIfSendingEmailToMySelf(user.getEmail(), usersEmails);
        checkUsersToIsOnCopy(usersEmails);
        checkRepeatedUsersToAndCopies(usersEmails);

        Boolean isNewEmailNonDraft = newEmail.getEmailStatus().equals(Email.EmailStatus.SENT);

        Email email = setOrCreateEmail(newEmail);
        email.setUsersEmails(usersEmails);

        List<UserEmail> userEmailsToCreate = new ArrayList<>();

        if (isNewEmailNonDraft) {
            setOrCreateUserEmailToOwner(email, userEmailsToCreate);
        }

        createUsersEmails(usersTo, email, userEmailsToCreate);

        return mountOutput(email);
    }

    private Email setOrCreateEmail(Email email) {
        email.setDisabled(false);
        email.setEmailStatus(Email.EmailStatus.SENT);
        email.setCreatedAt(new Date());

        setEmailUserEmailNullToNotConflict(email);

        return emailDataProvider.create(email);
    }

    private void setEmailUserEmailNullToNotConflict(Email email) {
        email.setUsersEmails(null);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private void createUsersEmails(List<User> usersTo, Email email, List<UserEmail> userEmailsToCreate) {
        List<UserEmail> userEmails = email.getUsersEmails();
        List<EmailOpeningOrder> emailOpeningOrders = new ArrayList<>();

        for (int i = 0; i <= usersTo.size() - 1; i++) {
            int index = i;

            Optional<UserEmail> userEmail = userEmails.stream().filter(ue -> ue.getUser().getEmail().equals(usersTo.get(index).getEmail())).findAny();

            if (userEmail.isEmpty()) {
                throw new RuntimeException("Unexpected Error while creating UsersEmails...");
            }

            userEmail.get().setOpened(false);
            userEmail.get().setUser(usersTo.get(i));
            userEmail.get().setEmail(email);
            userEmail.get().setEmailFlag(UserEmail.EmailFlag.INBOX);

            if (email.getOpeningOrders()) {
                if (userEmail.get().getEmailType().equals(UserEmail.EmailType.RECEIVED)) {
                    Optional<EmailOpeningOrder> firstOrderAlreadyCreated = emailOpeningOrders.stream().findFirst();
                    emailOpeningOrders.add(mountOpeningOrder(usersTo.get(i), email, i + 1));

                    if (firstOrderAlreadyCreated.isEmpty()) {
                        userEmailsToCreate.add(userEmail.get());
                    }
                }
            } else if (!userEmail.get().getEmailType().equals(UserEmail.EmailType.MINE)) {
                userEmailsToCreate.add(userEmail.get());
            }
        }

        if (!emailOpeningOrders.isEmpty()) {
            emailOpeningOrderDataProvider.createEmailOrders(emailOpeningOrders);
        }

        userEmailDataProvider.saveAll(userEmailsToCreate);
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

    private void checkIfSendingEmailToMySelf(String userOwnerEmail, List<UserEmail> userEmails) {
        List<String> usersOnCopy = userEmails.stream().filter(user -> user.getEmailType().equals(UserEmail.EmailType.IN_COPY)).map(user -> user.getUser().getEmail()).toList();
        List<String> usersTo = userEmails.stream().filter(user -> user.getEmailType().equals(UserEmail.EmailType.RECEIVED)).map(user -> user.getUser().getEmail()).toList();

        usersOnCopy.forEach(userOnCopy -> {
            if (userOnCopy.equals(userOwnerEmail)) {
                throw new SendingEmailToHimselfUsecase();
            }
        });

        usersTo.forEach(userTo -> {
            if (userTo.equals(userOwnerEmail)) {
                throw new SendingEmailToHimselfUsecase();
            }
        });
    }

    private void checkUsersToIsOnCopy(List<UserEmail> userEmails) {
        List<String> usersOnCopy = userEmails.stream().filter(user -> user.getEmailType().equals(UserEmail.EmailType.IN_COPY)).map(user -> user.getUser().getEmail()).toList();
        List<String> usersTo = userEmails.stream().filter(user -> user.getEmailType().equals(UserEmail.EmailType.RECEIVED)).map(user -> user.getUser().getEmail()).toList();

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

    private void checkRepeatedUsersToAndCopies(List<UserEmail> userEmails) {
        List<String> emailsTo = userEmails.stream().map(user -> user.getUser().getEmail()).toList();
        HashSet<String> userEmailNonRepeated = new HashSet<>(emailsTo);

        if (userEmailNonRepeated.size() != userEmails.size()) {
            throw new RepeatedUsersToOrCopyException();
        }
    }

    private void setOrCreateUserEmailToOwner(Email email, List<UserEmail> userEmailsToCreate) {
        UserEmail userEmailToOwner = new UserEmail(email.getUser(), email, false, false, UserEmail.EmailType.MINE);
        userEmailToOwner.setOpened(true);
        userEmailToOwner.setEmailFlag(UserEmail.EmailFlag.INBOX);

        userEmailsToCreate.add(userEmailToOwner);
    }

    private EmailOpeningOrder mountOpeningOrder(User user, Email email, Integer order) {
        return EmailOpeningOrder.builder()
            .order(order)
            .email(email)
            .user(user)
            .status(EmailOpeningOrder.OpeningStatus.NOT_OPENED)
            .build();
    }

    private EmailOutputDTO mountOutput(Email email) {
        return EmailOutputDTO.builder()
            .id(email.getId())
            .emailStatus(email.getEmailStatus())
            .sendFromName(email.getUser().getName())
            .sendFrom(email.getUser().getEmail())
            .sendFromProfilePicture(email.getUser().getProfilePicture())
            .createdAt(email.getCreatedAt())
            .hasOrder(email.getOpeningOrders())
            .message(email.getMessage())
            .subject(email.getSubject())
            .build();
    }
}
