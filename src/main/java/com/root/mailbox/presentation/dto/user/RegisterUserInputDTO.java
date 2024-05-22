package com.root.mailbox.presentation.dto.user;

import com.root.mailbox.domain.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterUserInputDTO {
    @NotBlank
    @Size(min = 3, message = "name must contain at least 3 characters")
    private String name;

    private String profilePicture;

    @NotBlank
    @Size(min = 6, message = "password must contain at least 6 characters")
    private String password;

    @NotBlank
    @Email
    private String email;

    public User toUser(){
        return User.builder()
            .name(this.name)
            .password(this.password)
            .email(this.email)
            .profilePicture(this.profilePicture)
            .build();
    }
}
