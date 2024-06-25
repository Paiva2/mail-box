package com.root.mailbox.presentation.dto.email;

import com.root.mailbox.domain.entities.Email;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.entities.UserEmail;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SendDraftEmailInputDTO {
    @NotEmpty
    private LinkedHashSet<String> toEmails;

    @NotNull
    private Boolean openingOrders;

    private List<String> copyList;

    public Email toEmail(UUID emailId) {
        List<UserEmail> usersInCopy = new ArrayList<>();

        if (Objects.nonNull(this.copyList) && !this.copyList.isEmpty()) {
            copyList.forEach(copy -> {
                UserEmail userEmailCopy = new UserEmail(User.builder().email(copy).build(), null, false, false, UserEmail.EmailType.IN_COPY);

                usersInCopy.add(userEmailCopy);
            });
        }

        List<UserEmail> usersEmailsToCreate = new ArrayList<>(usersInCopy);

        if (Objects.nonNull(this.toEmails) && !this.toEmails.isEmpty()) {
            this.toEmails.forEach(toEmail -> {
                UserEmail userEmail = new UserEmail(User.builder().email(toEmail).build(), null, false, false, UserEmail.EmailType.RECEIVED);

                usersEmailsToCreate.add(userEmail);
            });
        }

        return Email.builder()
            .id(emailId)
            .usersEmails(usersEmailsToCreate)
            .openingOrders(this.openingOrders)
            .build();
    }
}
