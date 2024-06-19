package com.root.mailbox.domain.usecases.trashBin;

import com.root.mailbox.domain.entities.*;
import com.root.mailbox.domain.exceptions.generic.BadRequestException;
import com.root.mailbox.domain.exceptions.generic.ForbiddenException;
import com.root.mailbox.domain.exceptions.trashBinUserEmail.EmailAlreadyAddedOnTrashBinException;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.domain.exceptions.userEmail.UserEmailAlreadyDisabledException;
import com.root.mailbox.domain.exceptions.userEmail.UserEmailNotFoundException;
import com.root.mailbox.infra.providers.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class SendUserEmailToTrashUsecase {
    private final UserDataProvider userDataProvider;
    private final UserEmailDataProvider userEmailDataProvider;
    private final EmailOpeningOrderDataProvider emailOpeningOrderDataProvider;
    private final TrashBinDataProvider trashBinDataProvider;
    private final TrashBinUserEmailDataProvider trashBinUserEmailDataProvider;

    @Transactional
    public void exec(Long userId, UUID emailId) {
        User user = checkIfUserExists(userId);
        Optional<TrashBin> trashBin = trashBinDataProvider.findByUser(user.getId());

        if (trashBin.isEmpty()) {
            throw new BadRequestException("Error while fetching user trash bin...");
        }

        if (user.getDisabled()) {
            throw new UserDisabledException(user.getId());
        }

        UserEmail userEmail = checkIfUserEmailExists(user, emailId);

        if (userEmail.getDisabled()) {
            throw new UserEmailAlreadyDisabledException();
        } else if (Objects.nonNull(userEmail.getDeletedAt())) {
            throw new UserEmailNotFoundException(emailId.toString(), user.getId().toString());
        }

        checkPermissions(user, userEmail);

        if (userEmail.getEmail().getOpeningOrders()) {
            handleUserEmailOrdering(userEmail);
        }

        updateToDisabled(userEmail);

        sendEmailToTrash(userEmail, trashBin.get());
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private UserEmail checkIfUserEmailExists(User user, UUID emailId) {
        return userEmailDataProvider.findUserEmail(user.getId(), emailId).orElseThrow(() -> new UserEmailNotFoundException(emailId.toString(), user.getId().toString()));
    }

    private void checkPermissions(User user, UserEmail userEmail) {
        Long userId = user.getId();
        Long userEmailUserId = userEmail.getUser().getId();

        if (!userEmailUserId.equals(userId)) {
            throw new ForbiddenException();
        }
    }

    private void handleUserEmailOrdering(UserEmail userEmail) {
        List<EmailOpeningOrder> emailOrders = getEmailOrders(userEmail.getEmail().getId());

        if (emailOrders.isEmpty()) return;

        Long userId = userEmail.getUser().getId();
        UUID emailId = userEmail.getEmail().getId();

        Optional<EmailOpeningOrder> orderEmail =
            emailOrders.stream().filter(order -> order.getUser().getId().equals(userId) && order.getEmail().getId().equals(emailId)).findAny();

        if (orderEmail.isEmpty()) {
            throw new BadRequestException("Error while handling with EmailOpeningOrder...");
        }

        orderEmail.get().setStatus(EmailOpeningOrder.OpeningStatus.OPENED);

        persistOrderEmailUpdated(orderEmail.get());

        if (emailOrders.size() > 1) {
            inviteNextFromOrder(emailOrders);
        }
    }

    private List<EmailOpeningOrder> getEmailOrders(UUID emailId) {
        return emailOpeningOrderDataProvider.findAllByEmail(emailId);
    }

    private void persistOrderEmailUpdated(EmailOpeningOrder emailOpeningOrder) {
        emailOpeningOrderDataProvider.update(emailOpeningOrder);
    }

    private void inviteNextFromOrder(List<EmailOpeningOrder> emailOrders) {
        Optional<EmailOpeningOrder> firstNonInvited = emailOrders.stream().filter(order -> order.getStatus().equals(EmailOpeningOrder.OpeningStatus.NOT_OPENED)).findFirst();

        if (firstNonInvited.isEmpty()) return;

        createUserEmailToNextOne(firstNonInvited.get().getUser(), firstNonInvited.get().getEmail());
    }

    private void createUserEmailToNextOne(User user, Email email) {
        UserEmail userEmail = new UserEmail(user, email, false, false, UserEmail.EmailType.RECEIVED);

        userEmail.setOpened(false);
        userEmail.setIsSpam(false);
        userEmail.setDisabled(false);

        persistNewUserEmail(userEmail);
    }

    private void persistNewUserEmail(UserEmail userEmail) {
        userEmailDataProvider.save(userEmail);
    }

    private void updateToDisabled(UserEmail userEmail) {
        userEmail.setDisabled(true);

        userEmailDataProvider.save(userEmail);
    }

    private void sendEmailToTrash(UserEmail userEmail, TrashBin trashBin) {
        User user = userEmail.getUser();
        Email email = userEmail.getEmail();

        checkIfUserEmailAlreadyOnTrash(user.getId(), email.getId(), trashBin.getId());

        TrashBinUserEmail trashBinUserEmail = new TrashBinUserEmail();
        trashBinUserEmail.setEmail(email);
        trashBinUserEmail.setUser(user);
        trashBinUserEmail.setTrashBin(trashBin);

        trashBinUserEmailDataProvider.create(trashBinUserEmail);
    }

    private void checkIfUserEmailAlreadyOnTrash(Long userId, UUID emailId, UUID trashBinId) {
        Optional<TrashBinUserEmail> doesUserEmailExists = trashBinUserEmailDataProvider.findByUserTrashAndEmailId(userId, emailId, trashBinId);

        if (doesUserEmailExists.isPresent()) {
            throw new EmailAlreadyAddedOnTrashBinException();
        }
    }
}
