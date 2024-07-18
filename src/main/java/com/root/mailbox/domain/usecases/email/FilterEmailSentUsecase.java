package com.root.mailbox.domain.usecases.email;

import com.root.mailbox.domain.entities.Email;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.entities.UserEmail;
import com.root.mailbox.domain.exceptions.email.EmailDisabledException;
import com.root.mailbox.domain.exceptions.email.EmailNotFoundException;
import com.root.mailbox.domain.exceptions.generic.ForbiddenException;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.infra.providers.EmailDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.presentation.dto.attachment.AttachmentOutputDTO;
import com.root.mailbox.presentation.dto.email.*;
import com.root.mailbox.presentation.dto.user.GetUserProfileOutputDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Service
public class FilterEmailSentUsecase {
    private final UserDataProvider userDataProvider;
    private final EmailDataProvider emailDataProvider;

    public EmailSentOutputDTO exec(Long userId, UUID emailId) {
        User user = checkIfUserExists(userId);

        if (user.getDisabled()) {
            throw new UserDisabledException(user.getId());
        }

        Email email = checkIfEmailExists(emailId, userId);

        handlePermissions(user, email);

        if (email.getDisabled()) {
            throw new EmailDisabledException(email.getId().toString());
        }

        return mountOutput(email);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private Email checkIfEmailExists(UUID emailId, Long userId) {
        return emailDataProvider.findByIdAndUserId(emailId, userId).orElseThrow(EmailNotFoundException::new);
    }

    private void handlePermissions(User user, Email email) {
        Long userId = user.getId();
        Long emailUserId = email.getUser().getId();

        if (!emailUserId.equals(userId)) {
            throw new ForbiddenException();
        }
    }

    private EmailSentOutputDTO mountOutput(Email email) {
        List<UserEmail> copiesInEmail = email.getUsersEmails().stream().filter(copy -> copy.getEmailType().equals(UserEmail.EmailType.IN_COPY)).toList();
        List<UserEmail> usersInEmail = email.getUsersEmails().stream().filter(copy -> copy.getEmailType().equals(UserEmail.EmailType.RECEIVED)).toList();

        return EmailSentOutputDTO.builder()
            .id(email.getId())
            .subject(email.getSubject())
            .message(email.getMessage())
            .createdAt(email.getCreatedAt())
            .hasOrder(email.getOpeningOrders())
            .attachments(email.getEmailAttachments().stream().map(
                    emailAttachment -> AttachmentOutputDTO.builder()
                        .id(emailAttachment.getAttachment().getId())
                        .fileName(emailAttachment.getAttachment().getFileName())
                        .url(emailAttachment.getAttachment().getUrl())
                        .createdAt(emailAttachment.getAttachment().getCreatedAt())
                        .build()
                ).toList()
            )
            .openingOrders(Objects.isNull(email.getEmailOpeningOrders()) ? null :
                email.getEmailOpeningOrders().stream().map(order ->
                    EmailOpeningOrderOutputDTO.builder()
                        .id(order.getId())
                        .status(order.getStatus())
                        .order(order.getOrder())
                        .user(GetUserProfileOutputDTO.builder()
                            .id(order.getUser().getId())
                            .name(order.getUser().getName())
                            .role(order.getUser().getRole())
                            .email(order.getUser().getEmail())
                            .profilePicture(order.getUser().getProfilePicture())
                            .createdAt(order.getUser().getCreatedAt())
                            .build()
                        )
                        .build()
                ).toList()
            )
            .usersReceivingEmailOutput(usersInEmail.stream().map(copy ->
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
                        .profilePicture(copy.getUser().getProfilePicture())
                        .createdAt(copy.getUser().getCreatedAt())
                        .build()
                ).toList()
            )
            .build();
    }
}
