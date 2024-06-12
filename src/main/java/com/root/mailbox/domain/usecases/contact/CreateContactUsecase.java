package com.root.mailbox.domain.usecases.contact;

import com.root.mailbox.domain.entities.Contact;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.exceptions.contact.UserAlreadyHasContactException;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.infra.providers.ContactDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.presentation.dto.contact.ContactOutputDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
@Service
public class CreateContactUsecase {
    private final ContactDataProvider contactDataProvider;
    private final UserDataProvider userDataProvider;

    public ContactOutputDTO exec(Contact newContact) {
        User user = checkIfUserExists(newContact.getUser().getId());

        if (user.getDisabled()) {
            throw new UserDisabledException(user.getId());
        }

        checkIfUserHasContactName(newContact.getName(), user.getId());
        newContact.setUser(user);
        newContact.setDisabled(false);

        Contact contact = persistNewContact(newContact);

        return mountOutput(contact);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private void checkIfUserHasContactName(String contactName, Long userId) {
        Optional<Contact> contact = contactDataProvider.findByUserIdAndName(contactName, userId);

        if (contact.isPresent()) {
            throw new UserAlreadyHasContactException();
        }
    }

    private Contact persistNewContact(Contact newContact) {
        return contactDataProvider.create(newContact);
    }

    private ContactOutputDTO mountOutput(Contact contact) {
        return ContactOutputDTO.builder()
            .id(contact.getId())
            .name(contact.getName())
            .email(contact.getEmail())
            .createdAt(contact.getCreatedAt())
            .build();
    }
}
