package com.root.mailbox.domain.usecases.contact;

import com.root.mailbox.domain.entities.Contact;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.exceptions.contact.ContactNotFoundException;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.infra.providers.ContactDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@AllArgsConstructor
@Service
public class DeleteContactUsecase {
    private final UserDataProvider userDataProvider;
    private final ContactDataProvider contactDataProvider;

    public void exec(Long userId, Long contactId) {
        User user = checkIfUserExists(userId);

        if (user.getDisabled()) {
            throw new UserDisabledException(userId);
        }

        Contact contactToDelete = checkIfContactExists(contactId, user.getId());

        checkPermissions(contactToDelete, user.getId());

        if (contactToDelete.getDisabled() || Objects.nonNull(contactToDelete.getDisabledAt())) {
            throw new ContactNotFoundException(contactToDelete.getId().toString());
        }

        disableContact(contactToDelete);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private Contact checkIfContactExists(Long contactId, Long userId) {
        return contactDataProvider.findByIdAndUser(contactId, userId).orElseThrow(() -> new ContactNotFoundException(contactId.toString()));
    }

    private void disableContact(Contact contact) {
        contact.setDisabled(true);
        contact.setDisabledAt(new Date());

        persistContact(contact);
    }

    private void persistContact(Contact contact) {
        contactDataProvider.create(contact);
    }

    private void checkPermissions(Contact contact, Long userId) {
        Long contactUserId = contact.getUser().getId();

        if (!contactUserId.equals(userId)) {
            throw new ContactNotFoundException(contact.getId().toString());
        }
    }
}
