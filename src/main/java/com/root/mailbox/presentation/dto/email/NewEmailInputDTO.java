package com.root.mailbox.presentation.dto.email;

import com.root.mailbox.domain.entities.Email;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.entities.UserEmail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NewEmailInputDTO {
    @NotEmpty
    private LinkedHashSet<String> toEmails;

    @NotBlank
    private String subject;

    @NotBlank
    private String message;

    @NotNull
    private Boolean openingOrders;

    private List<String> copyList;

    public Email toEmail() {
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
            .message(this.message)
            .subject(this.subject)
            .usersEmails(usersEmailsToCreate)
            .openingOrders(this.openingOrders)
            .emailStatus(Email.EmailStatus.SENT)
            .build();
    }
}
