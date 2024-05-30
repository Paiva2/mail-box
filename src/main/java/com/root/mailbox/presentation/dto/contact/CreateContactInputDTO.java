package com.root.mailbox.presentation.dto.contact;

import com.root.mailbox.domain.entities.Contact;
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
public class CreateContactInputDTO {
    @NotBlank
    @Size(min = 3)
    private String name;

    @Email
    @NotBlank
    private String email;

    public Contact toContact(Long userId) {
        return Contact.builder()
            .name(this.name)
            .email(this.email)
            .user(User.builder().id(userId).build())
            .build();
    }
}
