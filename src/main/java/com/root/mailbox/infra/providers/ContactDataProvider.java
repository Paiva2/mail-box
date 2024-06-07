package com.root.mailbox.infra.providers;

import com.root.mailbox.domain.entities.Contact;
import com.root.mailbox.infra.repositories.ContactRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<Contact> findAllByUserId(Long userId, String name, Pageable pageable) {
        return contactRepository.findAllByUserId(userId, name, pageable);
    }

    public Optional<Contact> findByIdAndUser(Long contactId, Long userId) {
        return contactRepository.findActiveByIdAndUserId(contactId, userId);
    }
}
