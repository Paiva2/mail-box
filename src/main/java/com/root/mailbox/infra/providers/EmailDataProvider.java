package com.root.mailbox.infra.providers;

import com.root.mailbox.domain.entities.Email;
import com.root.mailbox.domain.entities.UserEmail;
import com.root.mailbox.infra.repositories.EmailRepository;
import com.root.mailbox.infra.repositories.UserEmailRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class EmailDataProvider {
    private EmailRepository emailRepository;
    private UserEmailRepository userEmailRepository;

    public Email create(Email email) {
        return emailRepository.save(email);
    }

    public List<UserEmail> createUsersEmails(List<UserEmail> userEmail) {
        return userEmailRepository.saveAll(userEmail);
    }

    public Page<Email> findAllByUser(Long userId, String keyword, Pageable pageable) {
        return emailRepository.findAllByUserFiltering(userId, keyword, pageable);
    }

    public Optional<UserEmail> findUserEmailAsReceiver(Long userId, UUID emailId) {
        return userEmailRepository.findByUserAndEmail(userId, emailId);
    }

    public void markAsOpened(Long userId, UUID emailId) {
        userEmailRepository.markOpened(userId, emailId);
    }

    public Page<UserEmail> findAllUserEmailByUser(Long userId, String keyword, Boolean filteringSpam, Pageable pageable) {
        return userEmailRepository.findAllByUserId(userId, keyword, filteringSpam, pageable);
    }
}
