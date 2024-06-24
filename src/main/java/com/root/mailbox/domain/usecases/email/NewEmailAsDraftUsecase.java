package com.root.mailbox.domain.usecases.email;

import com.root.mailbox.domain.entities.Email;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.infra.providers.EmailDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.presentation.dto.email.EmailOutputDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class NewEmailAsDraftUsecase {
    private final UserDataProvider userDataProvider;
    private final EmailDataProvider emailDataProvider;

    public EmailOutputDTO exec(Long userId, Email email) {
        User user = checkIfUserExists(userId);

        if (user.getDisabled()) {
            throw new UserDisabledException(user.getId());
        }

        Email emailDraft = setEmailDraft(email, user);

        return mountOutput(emailDraft);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private Email setEmailDraft(Email email, User user) {
        email.setEmailStatus(Email.EmailStatus.DRAFT);
        email.setDisabled(false);
        email.setOpeningOrders(false);
        email.setUser(user);

        return emailDataProvider.create(email);
    }

    private EmailOutputDTO mountOutput(Email email) {
        return EmailOutputDTO.builder()
            .id(email.getId())
            .subject(email.getSubject())
            .message(email.getMessage())
            .hasOrder(email.getOpeningOrders())
            .createdAt(email.getCreatedAt())
            .emailStatus(email.getEmailStatus())
            .build();
    }
}
