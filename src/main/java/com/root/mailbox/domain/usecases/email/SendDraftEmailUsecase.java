package com.root.mailbox.domain.usecases.email;

import com.root.mailbox.domain.entities.Email;
import com.root.mailbox.domain.exceptions.email.EmailNonDraftException;
import com.root.mailbox.domain.exceptions.email.EmailNotFoundException;
import com.root.mailbox.infra.providers.EmailDataProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@AllArgsConstructor
@Service
public class SendDraftEmailUsecase {
    private final NewEmailUsecase newEmailUsecase;
    private final EmailDataProvider emailDataProvider;

    public void exec(Long userId, Email draftToSend) {
        Email draftEmailCreated = checkIfEmailDraftExists(draftToSend.getId());

        if (!draftEmailCreated.getEmailStatus().equals(Email.EmailStatus.DRAFT)) {
            throw new EmailNonDraftException();
        }

        setDraftEmail(draftToSend, draftEmailCreated);

        newEmailUsecase.exec(draftEmailCreated, userId);
    }

    private Email checkIfEmailDraftExists(UUID emailId) {
        return emailDataProvider.findDraftById(emailId).orElseThrow(EmailNotFoundException::new);
    }

    private void setDraftEmail(Email draftToSend, Email draftEmailCreated) {
        draftEmailCreated.setUsersEmails(draftToSend.getUsersEmails());
        draftEmailCreated.setOpeningOrders(draftToSend.getOpeningOrders());
    }
}
