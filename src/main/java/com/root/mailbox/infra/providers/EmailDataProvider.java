package com.root.mailbox.infra.providers;

import com.root.mailbox.domain.entities.Email;
import com.root.mailbox.domain.entities.UserEmail;
import com.root.mailbox.infra.repositories.EmailRepository;
import com.root.mailbox.infra.repositories.UserEmailRepository;
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
    private UserEmailRepository userEmailRepository;

    public Email create(Email email) {
        return emailRepository.save(email);
    }

    public UserEmail createUserEmail(UserEmail userEmail) {
        return userEmailRepository.save(userEmail);
    }

    public Page<Email> findAllByUser(Long userId, String keyword, Boolean filteringSpam, Pageable pageable) {
        return emailRepository.findAllByUserFiltering(userId, keyword, filteringSpam, pageable);
    }

    public Optional<UserEmail> findUserEmailAsReceiver(Long userId, UUID emailId) {
        return userEmailRepository.findByUserAndIdAndUserTo(userId, emailId);
    }

    public void markAsOpened(UUID emailId) {
        emailRepository.markOpened(emailId);
    }

    public Page<UserEmail> findAllUserEmailByUser(Long userId, String keyword, Pageable pageable) {
        return userEmailRepository.findAllByUserId(userId, keyword, pageable);
    }
}
