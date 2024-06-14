package com.root.mailbox.domain.usecases.email;

import com.root.mailbox.domain.entities.Email;
import com.root.mailbox.domain.entities.EmailOpeningOrder;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.entities.UserEmail;
import com.root.mailbox.domain.exceptions.generic.BadRequestException;
import com.root.mailbox.domain.exceptions.generic.ForbiddenException;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.domain.exceptions.userEmail.UserEmailAlreadyDisabledException;
import com.root.mailbox.domain.exceptions.userEmail.UserEmailNotFoundException;
import com.root.mailbox.infra.providers.EmailOpeningOrderDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.infra.providers.UserEmailDataProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class SendEmailToMeToTrashUsecase {
    private final UserDataProvider userDataProvider;
    private final UserEmailDataProvider userEmailDataProvider;
    private final EmailOpeningOrderDataProvider emailOpeningOrderDataProvider;

    @Transactional
    public void exec(Long userId, UUID emailId) {
        User user = checkIfUserExists(userId);

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
        UserEmail userEmail = new UserEmail(user, email, false, false);

        userEmail.setOpened(false);
        userEmail.setIsSpam(false);
        userEmail.setDisabled(false);

        persistNewUserEmail(userEmail);
    }

    private void persistNewUserEmail(UserEmail userEmail) {
        userEmailDataProvider.createUserEmail(userEmail);
    }

    private void updateToDisabled(UserEmail userEmail) {
        userEmail.setDisabled(true);

        userEmailDataProvider.createUserEmail(userEmail);
    }
}
