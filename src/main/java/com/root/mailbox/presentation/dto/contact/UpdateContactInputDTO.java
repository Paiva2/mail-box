package com.root.mailbox.presentation.dto.contact;

import com.root.mailbox.domain.entities.Contact;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateContactInputDTO {
    @Email
    private String email;

    @Size(min = 3)
    private String name;

    public Contact toContact() {
        return Contact.builder()
            .email(email)
            .name(name)
            .build();
    }
}
