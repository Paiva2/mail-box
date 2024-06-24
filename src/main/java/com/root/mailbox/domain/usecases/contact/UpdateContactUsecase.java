package com.root.mailbox.domain.usecases.contact;

import com.root.mailbox.domain.entities.Contact;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.exceptions.contact.ContactNotFoundException;
import com.root.mailbox.domain.exceptions.contact.UserAlreadyHasContactException;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.domain.utils.CopyClassProperties;
import com.root.mailbox.infra.providers.ContactDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.presentation.dto.contact.ContactOutputDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UpdateContactUsecase {
    private final UserDataProvider userDataProvider;
    private final ContactDataProvider contactDataProvider;
    private final CopyClassProperties<Contact> copyClassProperties;

    public ContactOutputDTO exec(Long userId, Long contactId, Contact contactUpdated) {
        User user = checkIfUserExists(userId);

        if (user.getDisabled()) {
            throw new UserDisabledException(user.getId());
        }

        Contact contact = checkIfContactExists(contactId, user.getId());

        if (contact.getDisabled()) {
            throw new ContactNotFoundException(contact.getId().toString());
        }

        checkPermissionsToEdit(user, contact);

        if (Objects.nonNull(contactUpdated.getName()) && !contact.getName().equals(contactUpdated.getName())) {
            checkIfAlreadyHasContactName(contactUpdated.getName(), user.getId());
        }

        copyClassProperties.copyNonNull(contactUpdated, contact);

        Contact updateContact = persistUpdatedContact(contact);

        return mountOutput(updateContact);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private Contact checkIfContactExists(Long contactId, Long userId) {
        return contactDataProvider.findByIdAndUser(contactId, userId).orElseThrow(() -> new ContactNotFoundException(contactId.toString()));
    }

    private void checkPermissionsToEdit(User user, Contact contact) {
        Long userId = user.getId();
        Long contactId = contact.getUser().getId();

        if (!userId.equals(contactId)) {
            throw new ContactNotFoundException(contact.getId().toString());
        }
    }

    private void checkIfAlreadyHasContactName(String name, Long userId) {
        Optional<Contact> contactAlreadyExists = contactDataProvider.findByUserIdAndName(name, userId);

        if (contactAlreadyExists.isPresent()) {
            throw new UserAlreadyHasContactException();
        }
    }

    private Contact persistUpdatedContact(Contact contact) {
        return contactDataProvider.create(contact);
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
