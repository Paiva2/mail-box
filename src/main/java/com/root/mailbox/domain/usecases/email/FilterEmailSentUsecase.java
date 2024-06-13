package com.root.mailbox.domain.usecases.email;

import com.root.mailbox.domain.entities.Email;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.exceptions.email.EmailDisabledException;
import com.root.mailbox.domain.exceptions.email.EmailNotFoundException;
import com.root.mailbox.domain.exceptions.generic.ForbiddenException;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.infra.providers.EmailDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.presentation.dto.email.CarbonCopyOutputDTO;
import com.root.mailbox.presentation.dto.email.EmailOpeningOrderOutputDTO;
import com.root.mailbox.presentation.dto.email.EmailSentOutputDTO;
import com.root.mailbox.presentation.dto.email.ListEmailsSentOutputDTO;
import com.root.mailbox.presentation.dto.user.GetUserProfileOutputDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
        return EmailSentOutputDTO.builder()
            .id(email.getId())
            .subject(email.getSubject())
            .message(email.getMessage())
            .createdAt(email.getCreatedAt())
            .hasOpeningOrder(email.getOpeningOrders())
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
            .ccs(Objects.isNull(email.getCCopies()) ? null :
                email.getCCopies().stream().map(copy ->
                    CarbonCopyOutputDTO.builder()
                        .id(copy.getId())
                        .user(GetUserProfileOutputDTO.builder()
                            .id(copy.getUser().getId())
                            .name(copy.getUser().getName())
                            .role(copy.getUser().getRole())
                            .email(copy.getUser().getEmail())
                            .profilePicture(copy.getUser().getProfilePicture())
                            .createdAt(copy.getUser().getCreatedAt())
                            .build()
                        )
                        .build()
                ).toList()
            )
            .build();
    }
}
