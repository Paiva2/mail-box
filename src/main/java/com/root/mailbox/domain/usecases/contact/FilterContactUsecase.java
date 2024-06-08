package com.root.mailbox.domain.usecases.contact;

import com.root.mailbox.domain.entities.Contact;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.exceptions.ContactNotFoundException;
import com.root.mailbox.domain.exceptions.UserNotFoundException;
import com.root.mailbox.infra.providers.ContactDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.presentation.dto.contact.ContactOutputDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FilterContactUsecase {
    private final UserDataProvider userDataProvider;
    private final ContactDataProvider contactDataProvider;

    public ContactOutputDTO exec(Long userId, Long contactId) {
        User user = checkIfUserExists(userId);
        Contact contact = checkIfContactExists(contactId, user.getId());

        checkIfContactBelongsToUser(contact, user);

        return mountOutput(contact);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private Contact checkIfContactExists(Long contactId, Long userId) {
        return contactDataProvider.findByIdAndUser(contactId, userId).orElseThrow(() -> new ContactNotFoundException(contactId.toString()));
    }

    private void checkIfContactBelongsToUser(Contact contact, User user) {
        if (!contact.getUser().getId().equals(user.getId())) {
            throw new ContactNotFoundException(contact.getId().toString());
        }
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
