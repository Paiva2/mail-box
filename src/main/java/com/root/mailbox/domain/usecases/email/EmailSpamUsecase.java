package com.root.mailbox.domain.usecases.email;

import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.entities.UserEmail;
import com.root.mailbox.domain.exceptions.generic.ForbiddenException;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.userEmail.UserEmailNotFoundException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.infra.providers.EmailDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.infra.providers.UserEmailDataProvider;
import com.root.mailbox.presentation.dto.attachment.AttachmentOutputDTO;
import com.root.mailbox.presentation.dto.email.CarbonCopyOutputDTO;
import com.root.mailbox.presentation.dto.email.EmailOutputDTO;
import com.root.mailbox.presentation.dto.email.UserReceivingEmailOutputDTO;
import com.root.mailbox.presentation.dto.user.GetUserProfileOutputDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Service
public class EmailSpamUsecase {
    private final UserDataProvider userDataProvider;
    private final UserEmailDataProvider userEmailDataProvider;

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
        return userEmailDataProvider.findUserEmail(userId, emailId).orElseThrow(() -> new UserEmailNotFoundException(emailId.toString(), userId.toString()));
    }

    private void checkPermissions(User user, UserEmail userEmail) {
        Long userId = user.getId();
        Long userEmailId = userEmail.getUser().getId();

        if (!userEmailId.equals(userId)) {
            throw new ForbiddenException();
        }
    }

    private UserEmail setChanges(UserEmail userEmail) {
        return userEmailDataProvider.handleUserEmailSpam(userEmail);
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
            .attachments(userEmail.getEmail().getEmailAttachments().stream().map(
                    emailAttachment -> AttachmentOutputDTO.builder()
                        .id(emailAttachment.getAttachment().getId())
                        .fileName(emailAttachment.getAttachment().getFileName())
                        .url(emailAttachment.getAttachment().getUrl())
                        .createdAt(emailAttachment.getAttachment().getCreatedAt())
                        .build()
                ).toList()
            )
            .usersReceivingEmailOutput(usersInEmail.stream().map(copy ->
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
