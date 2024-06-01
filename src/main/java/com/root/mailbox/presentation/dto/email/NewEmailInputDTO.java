package com.root.mailbox.presentation.dto.email;

import com.root.mailbox.domain.entities.CarbonCopy;
import com.root.mailbox.domain.entities.Email;
import com.root.mailbox.domain.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NewEmailInputDTO {
    private String toEmail;
    private String subject;
    private String message;
    private List<String> copyList;

    public Email toEmail() {
        List<CarbonCopy> usersInCopy = Objects.isNull(this.copyList) ? null : this.copyList.stream()
            .map(copyEmail -> CarbonCopy.builder().user(
                    User.builder()
                        .email(copyEmail)
                        .build()
                ).build()
            ).toList();

        return Email.builder()
            .message(this.message)
            .subject(this.subject)
            .userTo(User.builder().email(this.toEmail).build())
            .cCopies(usersInCopy)
            .build();
    }
}
