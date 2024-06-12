package com.root.mailbox.domain.usecases.email;

import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.entities.UserEmail;
import com.root.mailbox.domain.exceptions.ForbiddenException;
import com.root.mailbox.domain.exceptions.UserDisabledException;
import com.root.mailbox.domain.exceptions.UserEmailNotFoundException;
import com.root.mailbox.domain.exceptions.UserNotFoundException;
import com.root.mailbox.infra.providers.EmailDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.presentation.dto.email.CarbonCopyOutputDTO;
import com.root.mailbox.presentation.dto.email.EmailOutputDTO;
import com.root.mailbox.presentation.dto.user.GetUserProfileOutputDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Service
public class EmailSpamUsecase {
    private final UserDataProvider userDataProvider;
    private final EmailDataProvider emailDataProvider;

    public EmailOutputDTO exec(Long userId, UUID emailId, Boolean setSpam) {
        User user = checkIfUserExists(userId);
        UserEmail userEmail = checkIfUserEmailExists(user.getId(), emailId);

        if (user.getDisabled()) {
            throw new UserDisabledException(user.getId());
        }

        if (Objects.nonNull(userEmail.getDeletedAt())) {
            throw new UserEmailNotFoundException(emailId.toString(), userId.toString());
        }

        checkPermissions(user, userEmail);

        Boolean isChangingToSameStateAsCurrent = userEmail.getIsSpam().equals(setSpam);

        if (isChangingToSameStateAsCurrent) {
            return mountOutput(userEmail);
        }

        userEmail.setIsSpam(setSpam);
        UserEmail userEmailUpdated = setChanges(userEmail);

        return mountOutput(userEmailUpdated);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private UserEmail checkIfUserEmailExists(Long userId, UUID emailId) {
        return emailDataProvider.findUserEmail(userId, emailId).orElseThrow(() -> new UserEmailNotFoundException(emailId.toString(), userId.toString()));
    }

    private void checkPermissions(User user, UserEmail userEmail) {
        Long userId = user.getId();
        Long userEmailId = userEmail.getUser().getId();

        if (!userEmailId.equals(userId)) {
            throw new ForbiddenException();
        }
    }

    private UserEmail setChanges(UserEmail userEmail) {
        return emailDataProvider.handleUserEmailSpam(userEmail);
    }

    private EmailOutputDTO mountOutput(UserEmail userEmail) {
        return EmailOutputDTO.builder()
            .id(userEmail.getEmail().getId())
            .subject(userEmail.getEmail().getSubject())
            .message(userEmail.getEmail().getMessage())
            .opened(userEmail.getOpened())
            .isSpam(userEmail.getIsSpam())
            .hasOrder(userEmail.getEmail().getOpeningOrders())
            .createdAt(userEmail.getEmail().getCreatedAt())
            .ccs(userEmail.getEmail().getCCopies().stream().map(copy ->
                CarbonCopyOutputDTO.builder()
                    .id(copy.getId())
                    .user(GetUserProfileOutputDTO.builder()
                        .id(copy.getUser().getId())
                        .email(copy.getUser().getEmail())
                        .name(copy.getUser().getName())
                        .profilePicture(copy.getUser().getProfilePicture())
                        .role(copy.getUser().getRole())
                        .createdAt(copy.getUser().getCreatedAt())
                        .build()
                    )
                    .build()
            ).toList())
            .build();
    }
}
