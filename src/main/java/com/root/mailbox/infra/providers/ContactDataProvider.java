package com.root.mailbox.infra.providers;

import com.root.mailbox.domain.entities.Contact;
import com.root.mailbox.infra.repositories.ContactRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class ContactDataProvider {
    private final ContactRepository contactRepository;

    public Optional<Contact> findByUserIdAndName(String name, Long userId) {
        return contactRepository.findByNameAndUserId(name, userId);
    }

    public Contact create(Contact newContact) {
        return contactRepository.save(newContact);
    }
}
