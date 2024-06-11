package com.root.mailbox.domain.usecases.email;

import com.root.mailbox.domain.entities.Email;
import com.root.mailbox.domain.entities.EmailOpeningOrder;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.entities.UserEmail;
import com.root.mailbox.domain.exceptions.EmailNotFoundException;
import com.root.mailbox.domain.exceptions.OpeningOrderNotFoundException;
import com.root.mailbox.domain.exceptions.UserEmailNotFoundException;
import com.root.mailbox.domain.exceptions.UserNotFoundException;
import com.root.mailbox.infra.providers.EmailDataProvider;
import com.root.mailbox.infra.providers.EmailOpeningOrderDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.presentation.dto.email.CarbonCopyOutputDTO;
import com.root.mailbox.presentation.dto.email.EmailOutputDTO;
import com.root.mailbox.presentation.dto.user.GetUserProfileOutputDTO;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class FilterEmailToMeUsecase {
    private final UserDataProvider userDataProvider;
    private final EmailDataProvider emailDataProvider;
    private final EmailOpeningOrderDataProvider emailOpeningOrderDataProvider;

    @Transactional
    public EmailOutputDTO exec(Long userId, UUID userEmailId) {
        User user = checkIfUserExists(userId);
        UserEmail userEmail = checkIfEmailExists(userEmailId, user.getId());
        Email email = userEmail.getEmail();

        if (Objects.isNull(userEmail.getEmail())) {
            throw new EmailNotFoundException();
        }

        if (!userEmail.getOpened()) {
            markEmailAsOpened(userEmail);

            if (email.getOpeningOrders()) {
                List<EmailOpeningOrder> openingOrders = getAllEmailOrders(userEmail);

                EmailOpeningOrder openingOrder = checkIfOpeningOrderExists(userEmail, openingOrders);
                openingOrder.setStatus(EmailOpeningOrder.OpeningStatus.OPENED);

                persistOpeningOrderUpdated(openingOrder);
                handleNextOrders(openingOrder, openingOrders);
            }
        }

        return mountOutput(userEmail);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private UserEmail checkIfEmailExists(UUID emailId, Long userId) {
        return emailDataProvider.findUserEmailAsReceiver(userId, emailId).orElseThrow(() -> new UserEmailNotFoundException(emailId.toString(), userId.toString()));
    }

    private List<EmailOpeningOrder> getAllEmailOrders(UserEmail userEmail) {
        return emailOpeningOrderDataProvider.findAllByEmail(userEmail.getEmail().getId());
    }

    private EmailOpeningOrder checkIfOpeningOrderExists(UserEmail userEmail, List<EmailOpeningOrder> emailOrders) {
        Long userId = userEmail.getUser().getId();
        UUID emailId = userEmail.getEmail().getId();

        return emailOrders.stream().filter(order -> order.getUser().getId().equals(userId)).findFirst()
            .orElseThrow(() -> new OpeningOrderNotFoundException(userId.toString(), emailId.toString()));
    }

    private void markEmailAsOpened(UserEmail userEmail) {
        emailDataProvider.markAsOpened(userEmail.getUser().getId(), userEmail.getEmail().getId());
    }

    private void persistOpeningOrderUpdated(EmailOpeningOrder emailOpeningOrder) {
        emailOpeningOrderDataProvider.update(emailOpeningOrder);
    }

    private void handleNextOrders(EmailOpeningOrder currentOpening, List<EmailOpeningOrder> emailOpeningOrders) {
        Integer currentNumber = currentOpening.getOrder();

        Optional<EmailOpeningOrder> findNext = emailOpeningOrders.stream().filter(order -> order.getStatus().equals(EmailOpeningOrder.OpeningStatus.NOT_OPENED) && order.getOrder() > currentNumber).findFirst();

        if (findNext.isEmpty()) return;

        sendEmailToNext(findNext.get().getEmail(), findNext.get().getUser());
    }

    private void sendEmailToNext(Email email, User user) {
        UserEmail userEmail = new UserEmail(user, email, false, false);
        userEmail.setOpened(false);
        userEmail.setDisabled(false);
        userEmail.setIsSpam(false);

        emailDataProvider.createUserEmail(userEmail);
    }

    private EmailOutputDTO mountOutput(UserEmail userEmail) {
        return EmailOutputDTO.builder()
            .id(userEmail.getEmail().getId())
            .message(userEmail.getEmail().getMessage())
            .subject(userEmail.getEmail().getSubject())
            .isSpam(userEmail.getIsSpam())
            .opened(userEmail.getOpened())
            .hasOrder(userEmail.getEmail().getOpeningOrders())
            .createdAt(userEmail.getEmail().getCreatedAt())
            .ccs(userEmail.getEmail().getCCopies().stream().map(copy ->
                    CarbonCopyOutputDTO.builder()
                        .id(copy.getId())
                        .user(
                            GetUserProfileOutputDTO.builder()
                                .id(copy.getUser().getId())
                                .name(copy.getUser().getName())
                                .role(copy.getUser().getRole())
                                .email(copy.getUser().getEmail())
                                .createdAt(copy.getUser().getCreatedAt())
                                .profilePicture(copy.getUser().getProfilePicture())
                                .build()
                        ).build()
                ).toList()
            ).build();
    }
}
