package com.root.mailbox.infra.providers;

import com.root.mailbox.domain.entities.Email;
import com.root.mailbox.infra.repositories.EmailRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class EmailDataProvider {
    private EmailRepository emailRepository;

    public Email create(Email email) {
        return emailRepository.save(email);
    }

    public Page<Email> findAllByUser(Long userId, String keyword, Pageable pageable) {
        return emailRepository.findAllByUserFiltering(userId, keyword, pageable);
    }

    public Optional<Email> findByIdAndUserId(UUID emailId, Long userId) {
        return emailRepository.findByIdAndUser(emailId, userId);
    }

    public Optional<Email> findById(UUID emailId) {
        return emailRepository.findById(emailId);
    }

    public Page<Email> findAllDraftByUser(Long userId, String keyword, Pageable pageable) {
        return emailRepository.findAllDraftsByUser(userId, keyword, pageable);
    }

    public void deleteById(UUID emailId) {
        emailRepository.deleteById(emailId);
    }
}
