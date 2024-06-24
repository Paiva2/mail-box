package com.root.mailbox.presentation.controllers.contact;

import com.root.mailbox.domain.usecases.contact.*;
import com.root.mailbox.presentation.dto.contact.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ContactControllerImpl implements ContactController {
    private final CreateContactUsecase createContactUsecase;
    private final ListContactsUsecase listContactsUsecase;
    private final FilterContactUsecase filterContactUsecase;
    private final DeleteContactUsecase deleteContactUsecase;
    private final UpdateContactUsecase updateContactUsecase;

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

    @Override
    public ResponseEntity<ContactOutputDTO> filter(
        Authentication authentication,
        @PathVariable("contactId") Long contactId
    ) {
        Long userId = Long.valueOf(authentication.getName());
        ContactOutputDTO output = filterContactUsecase.exec(userId, contactId);

        return new ResponseEntity<>(output, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> delete(
        Authentication authentication,
        @PathVariable("contactId") Long contactId
    ) {
        Long userId = Long.valueOf(authentication.getName());
        deleteContactUsecase.exec(userId, contactId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<ContactOutputDTO> update(
        Authentication authentication,
        @PathVariable("contactId") Long contactId,
        @RequestBody @Valid UpdateContactInputDTO dto) {
        Long userId = Long.valueOf(authentication.getName());
        ContactOutputDTO output = updateContactUsecase.exec(userId, contactId, dto.toContact());

        return new ResponseEntity<>(output, HttpStatus.OK);
    }
}
