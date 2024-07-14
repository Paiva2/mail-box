package com.root.mailbox.domain.usecases.email;

import com.root.mailbox.domain.entities.Email;
import com.root.mailbox.domain.entities.EmailOpeningOrder;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.entities.UserEmail;
import com.root.mailbox.domain.exceptions.email.EmailNotFoundException;
import com.root.mailbox.domain.exceptions.openingOrder.OpeningOrderNotFoundException;
import com.root.mailbox.domain.exceptions.userEmail.UserEmailNotFoundException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.infra.providers.EmailOpeningOrderDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.infra.providers.UserEmailDataProvider;
import com.root.mailbox.presentation.dto.attachment.AttachmentOutputDTO;
import com.root.mailbox.presentation.dto.email.CarbonCopyOutputDTO;
import com.root.mailbox.presentation.dto.email.EmailOutputDTO;
import com.root.mailbox.presentation.dto.email.UserReceivingEmailOutputDTO;
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
    private final UserEmailDataProvider userEmailDataProvider;
    private final EmailOpeningOrderDataProvider emailOpeningOrderDataProvider;

    @Transactional
    public EmailOutputDTO exec(Long userId, UUID userEmailId) {
        User user = checkIfUserExists(userId);
        UserEmail userEmail = checkIfUserEmailExists(userEmailId, user.getId());
        Email email = userEmail.getEmail();

        if (Objects.isNull(userEmail.getEmail())) {
            throw new EmailNotFoundException();
        }

        if (!userEmail.getOpened()) {
            markEmailAsOpened(userEmail);

            if (email.getOpeningOrders()) {
                List<EmailOpeningOrder> openingOrders = getAllEmailOrders(userEmail);

                EmailOpeningOrder openingOrder = checkIfOpeningOrderExists(userEmail, openingOrders);

                if (openingOrder.getStatus().equals(EmailOpeningOrder.OpeningStatus.NOT_OPENED)) {
                    openingOrder.setStatus(EmailOpeningOrder.OpeningStatus.OPENED);

                    persistOpeningOrderUpdated(openingOrder);
                    handleNextOrders(openingOrder, openingOrders);
                }
            }
        }

        return mountOutput(userEmail);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private UserEmail checkIfUserEmailExists(UUID emailId, Long userId) {
        return userEmailDataProvider.findUserEmailAsReceiver(userId, emailId).orElseThrow(() -> new UserEmailNotFoundException(emailId.toString(), userId.toString()));
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
        userEmailDataProvider.markAsOpened(userEmail.getUser().getId(), userEmail.getEmail().getId());
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
        UserEmail userEmail = new UserEmail(user, email, false, false, UserEmail.EmailType.RECEIVED);
        userEmail.setOpened(false);
        userEmail.setDisabled(false);
        userEmail.setIsSpam(false);
        userEmail.setEmailFlag(UserEmail.EmailFlag.INBOX);

        userEmailDataProvider.save(userEmail);
    }

    private EmailOutputDTO mountOutput(UserEmail userEmail) {
        List<UserEmail> copiesInEmail = userEmail.getEmail().getUsersEmails().stream().filter(copy -> copy.getEmailType().equals(UserEmail.EmailType.IN_COPY)).toList();
        List<UserEmail> usersInEmail = userEmail.getEmail().getUsersEmails().stream().filter(copy -> copy.getEmailType().equals(UserEmail.EmailType.RECEIVED)).toList();

        return EmailOutputDTO.builder()
            .id(userEmail.getEmail().getId())
            .message(userEmail.getEmail().getMessage())
            .subject(userEmail.getEmail().getSubject())
            .isSpam(userEmail.getIsSpam())
            .opened(userEmail.getOpened())
            .hasOrder(userEmail.getEmail().getOpeningOrders())
            .createdAt(userEmail.getEmail().getCreatedAt())
            .emailStatus(userEmail.getEmail().getEmailStatus())
            .sendFrom(userEmail.getEmail().getUser().getEmail())
            .sendFromName(userEmail.getEmail().getUser().getName())
            .attachments(userEmail.getEmail().getAttachments().stream().map(
                    attachment -> AttachmentOutputDTO.builder()
                        .id(attachment.getId())
                        .fileName(attachment.getFileName())
                        .url(attachment.getUrl())
                        .extension(attachment.getExtension())
                        .createdAt(attachment.getCreatedAt())
                        .build()
                ).toList()
            )
            .userReceivingEmailOutput(usersInEmail.stream().map(copy ->
                    UserReceivingEmailOutputDTO.builder()
                        .id(copy.getUser().getId())
                        .name(copy.getUser().getName())
                        .email(copy.getUser().getEmail())
                        .createdAt(copy.getUser().getCreatedAt())
                        .profilePicture(copy.getUser().getProfilePicture())
                        .build()
                ).toList()
            )
            .ccs(copiesInEmail.stream().map(copy ->
                    CarbonCopyOutputDTO.builder()
                        .id(copy.getUser().getId())
                        .name(copy.getUser().getName())
                        .email(copy.getUser().getEmail())
                        .createdAt(copy.getUser().getCreatedAt())
                        .profilePicture(copy.getUser().getProfilePicture())
                        .build()
                ).toList()
            )
            .build();
    }
}
