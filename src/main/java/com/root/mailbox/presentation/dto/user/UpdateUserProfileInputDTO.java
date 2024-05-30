package com.root.mailbox.presentation.dto.user;


import com.root.mailbox.domain.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateUserProfileInputDTO {
    @Email
    private String email;

    @Size(min = 3)
    private String name;

    @Size(min = 6)
    private String password;

    private String profilePicture;
    
    public User toUser(Long id) {
        return User.builder()
            .id(id)
            .password(this.password)
            .name(this.name)
            .email(this.email)
            .profilePicture(this.profilePicture)
            .build();
    }
}
