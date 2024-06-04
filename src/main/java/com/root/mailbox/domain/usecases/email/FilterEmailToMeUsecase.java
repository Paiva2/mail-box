package com.root.mailbox.domain.usecases.email;

import com.root.mailbox.domain.entities.Email;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.entities.UserEmail;
import com.root.mailbox.domain.exceptions.UserEmailNotFoundException;
import com.root.mailbox.domain.exceptions.UserNotFoundException;
import com.root.mailbox.infra.providers.EmailDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.presentation.dto.email.CarbonCopyOutputDTO;
import com.root.mailbox.presentation.dto.email.EmailOutputDTO;
import com.root.mailbox.presentation.dto.user.GetUserProfileOutputDTO;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@AllArgsConstructor
@Service
public class FilterEmailToMeUsecase {
    private final UserDataProvider userDataProvider;
    private final EmailDataProvider emailDataProvider;

    @Transactional
    public EmailOutputDTO exec(Long userId, UUID emailId) {
        User user = checkIfUserExists(userId);
        UserEmail userEmail = checkIfEmailExists(emailId, user.getId());
        Email email = userEmail.getEmail();

        if (!email.getOpened()) {
            markEmailAsOpened(email);
        }

        return mountOutput(email);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private UserEmail checkIfEmailExists(UUID emailId, Long userId) {
        return emailDataProvider.findUserEmailAsReceiver(userId, emailId).orElseThrow(() -> new UserEmailNotFoundException(emailId.toString(), userId.toString()));
    }

    private void markEmailAsOpened(Email email) {
        emailDataProvider.markAsOpened(email.getId());
    }

    private EmailOutputDTO mountOutput(Email email) {
        return EmailOutputDTO.builder()
            .id(email.getId())
            .message(email.getMessage())
            .subject(email.getSubject())
            .isSpam(email.getIsSpam())
            .opened(email.getOpened())
            .createdAt(email.getCreatedAt())
            .ccs(email.getCCopies().stream().map(copy ->
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
