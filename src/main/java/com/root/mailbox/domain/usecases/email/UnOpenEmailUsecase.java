package com.root.mailbox.domain.usecases.email;

import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.entities.UserEmail;
import com.root.mailbox.domain.exceptions.generic.ForbiddenException;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.domain.exceptions.userEmail.UserEmailNotFoundException;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.infra.providers.UserEmailDataProvider;
import com.root.mailbox.presentation.dto.email.CarbonCopyOutputDTO;
import com.root.mailbox.presentation.dto.email.EmailOutputDTO;
import com.root.mailbox.presentation.dto.email.UserReceivingEmailOutputDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class UnOpenEmailUsecase {
    private final UserDataProvider userDataProvider;
    private final UserEmailDataProvider userEmailDataProvider;

    public EmailOutputDTO exec(Long userId, UUID emailId) {
        User user = checkIfUserExists(userId);

        if (user.getDisabled()) {
            throw new UserDisabledException(user.getId());
        }

        UserEmail userEmail = checkIfUserEmailExists(user.getId(), emailId);

        if (userEmail.getDisabled()) {
            throw new UserEmailNotFoundException(emailId.toString(), userId.toString());
        }

        checkPermissions(userId, userEmail);
        handleEmailOpening(userEmail);

        UserEmail userEmailUpdated = persistUpdatedEmail(userEmail);

        return mountOutput(userEmailUpdated);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private UserEmail checkIfUserEmailExists(Long userId, UUID emailId) {
        return userEmailDataProvider.findUserEmail(userId, emailId).orElseThrow(() -> new UserEmailNotFoundException(emailId.toString(), userId.toString()));
    }

    private void checkPermissions(Long userId, UserEmail userEmail) {
        Long userEmailUserId = userEmail.getUser().getId();

        if (!userEmailUserId.equals(userId)) {
            throw new ForbiddenException();
        }
    }

    private void handleEmailOpening(UserEmail userEmail) {
        if (!userEmail.getOpened()) return;

        userEmail.setOpened(false);
    }

    private UserEmail persistUpdatedEmail(UserEmail userEmail) {
        return userEmailDataProvider.save(userEmail);
    }

    private EmailOutputDTO mountOutput(UserEmail userEmail) {
        List<UserEmail> copiesInEmail = userEmail.getEmail().getUsersEmails().stream().filter(copy -> copy.getEmailType().equals(UserEmail.EmailType.IN_COPY)).toList();
        List<UserEmail> usersInEmail = userEmail.getEmail().getUsersEmails().stream().filter(copy -> copy.getEmailType().equals(UserEmail.EmailType.RECEIVED)).toList();

        return EmailOutputDTO.builder()
            .id(userEmail.getEmail().getId())
            .subject(userEmail.getEmail().getSubject())
            .message(userEmail.getEmail().getMessage())
            .opened(userEmail.getOpened())
            .isSpam(userEmail.getIsSpam())
            .hasOrder(userEmail.getEmail().getOpeningOrders())
            .createdAt(userEmail.getEmail().getCreatedAt())
            .emailStatus(userEmail.getEmail().getEmailStatus())
            .userReceivingEmailOutput(usersInEmail.stream().map(copy ->
                    UserReceivingEmailOutputDTO.builder()
                        .id(copy.getUser().getId())
                        .email(copy.getUser().getEmail())
                        .name(copy.getUser().getName())
                        .profilePicture(copy.getUser().getProfilePicture())
                        .createdAt(copy.getUser().getCreatedAt())
                        .build()
                ).toList()
            )
            .ccs(copiesInEmail.stream().map(copy ->
                    CarbonCopyOutputDTO.builder()
                        .id(copy.getUser().getId())
                        .email(copy.getUser().getEmail())
                        .name(copy.getUser().getName())
                        .profilePicture(copy.getUser().getProfilePicture())
                        .createdAt(copy.getUser().getCreatedAt())
                        .build()
                ).toList()
            )
            .build();
    }
}
