package com.root.mailbox.presentation.dto.email;

import com.root.mailbox.domain.entities.CarbonCopy;
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
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NewEmailInputDTO {
    @NotEmpty
    private List<String> toEmails;

    @NotBlank
    private String subject;

    @NotBlank
    private String message;

    @NotNull
    private Boolean openingOrders;

    private List<String> copyList;

    public Email toEmail() {
        List<UserEmail> usersToReceive = Objects.isNull(this.toEmails) ? new ArrayList<>() :
            this.toEmails.stream().map(toEmail -> new UserEmail(User.builder().email(toEmail).build(), null, false, false)).toList();

        List<CarbonCopy> usersInCopy = Objects.isNull(this.copyList) ? new ArrayList<>() : this.copyList.stream()
            .map(copyEmail -> CarbonCopy.builder().user(
                    User.builder()
                        .email(copyEmail)
                        .build()
                ).build()
            ).toList();

        return Email.builder()
            .message(this.message)
            .subject(this.subject)
            .usersEmails(usersToReceive)
            .cCopies(usersInCopy)
            .openingOrders(this.openingOrders)
            .build();
    }
}
