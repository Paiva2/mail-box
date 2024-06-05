package com.root.mailbox.presentation.controllers.contact;

import com.root.mailbox.domain.usecases.contacts.CreateContactUsecase;
import com.root.mailbox.domain.usecases.contacts.ListContactsUsecase;
import com.root.mailbox.presentation.dto.contact.ContactOutputDTO;
import com.root.mailbox.presentation.dto.contact.CreateContactInputDTO;
import com.root.mailbox.presentation.dto.contact.ListContactsPaginationInputDTO;
import com.root.mailbox.presentation.dto.contact.ListContactsPaginationOutputDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ContactControllerImpl implements ContactController {
    private final CreateContactUsecase createContactUsecase;
    private final ListContactsUsecase listContactsUsecase;

    @Override
    public ResponseEntity<ContactOutputDTO> create(
        Authentication authentication,
        @RequestBody @Valid CreateContactInputDTO dto
    ) {
        Long userId = Long.valueOf(authentication.getName());
        ContactOutputDTO output = createContactUsecase.exec(dto.toContact(userId));

        return new ResponseEntity<>(output, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ListContactsPaginationOutputDTO> listAll(
        Authentication authentication,
        @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
        @RequestParam(name = "size", required = false, defaultValue = "5") Integer size,
        @RequestParam(name = "name", required = false) String name
    ) {
        Long userId = Long.valueOf(authentication.getName());
        ListContactsPaginationOutputDTO output = listContactsUsecase.exec(userId, ListContactsPaginationInputDTO
            .builder()
            .page(page)
            .size(size)
            .name(name)
            .build()
        );

        return new ResponseEntity<>(output, HttpStatus.OK);
    }
}
