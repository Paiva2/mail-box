package com.root.mailbox.presentation.dto.user;

import com.root.mailbox.domain.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthenticateUserInputDTO {
    @Email
    @NotBlank
    private String email;

    @Size(min = 6, message = "password must have at leasr 6 characters")
    @NotBlank
    private String password;

    public User toUser() {
        return User.builder()
            .email(this.email)
            .password(this.password)
            .build();
    }
}
