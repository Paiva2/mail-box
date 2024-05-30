package com.root.mailbox.presentation.controllers.contact;

import com.root.mailbox.domain.usecases.contacts.CreateContactUsecase;
import com.root.mailbox.presentation.dto.contact.ContactOutputDTO;
import com.root.mailbox.presentation.dto.contact.CreateContactInputDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ContactControllerImpl implements ContactController {
    private final CreateContactUsecase createContactUsecase;

    @Override
    public ResponseEntity<ContactOutputDTO> create(
        Authentication authentication,
        @RequestBody @Valid CreateContactInputDTO dto
    ) {
        Long userId = Long.valueOf(authentication.getName());
        ContactOutputDTO output = createContactUsecase.exec(dto.toContact(userId));

        return new ResponseEntity<>(output, HttpStatus.CREATED);
    }
}
