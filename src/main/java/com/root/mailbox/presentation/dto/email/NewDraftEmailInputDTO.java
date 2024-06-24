package com.root.mailbox.presentation.dto.email;

import com.root.mailbox.domain.entities.Email;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.entities.UserEmail;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NewDraftEmailInputDTO {
    @NotBlank
    private String subject;

    @NotBlank
    private String message;

    public Email toEmail() {
        return Email.builder()
            .message(this.message)
            .subject(this.subject)
            .build();
    }
}
