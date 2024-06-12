package com.root.mailbox.infra.providers;

import com.root.mailbox.domain.entities.UserEmail;
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
public class UserEmailDataProvider {
    private final UserEmailRepository userEmailRepository;

    public List<UserEmail> createUsersEmails(List<UserEmail> userEmail) {
        return userEmailRepository.saveAll(userEmail);
    }

    public UserEmail createUserEmail(UserEmail userEmail) {
        return userEmailRepository.save(userEmail);
    }

    public Optional<UserEmail> findUserEmailAsReceiver(Long userId, UUID emailId) {
        return userEmailRepository.findByUserAndEmailReceiving(userId, emailId);
    }

    public void markAsOpened(Long userId, UUID emailId) {
        userEmailRepository.markOpened(userId, emailId);
    }

    public Page<UserEmail> findAllUserEmailByUser(Long userId, String keyword, Boolean filteringSpam, Pageable pageable) {
        return userEmailRepository.findAllByUserId(userId, keyword, filteringSpam, pageable);
    }

    public Optional<UserEmail> findUserEmail(Long userId, UUID emailId) {
        return userEmailRepository.findByUserIdAndEmailId(userId, emailId);
    }

    public UserEmail handleUserEmailSpam(UserEmail userEmail) {
        return userEmailRepository.save(userEmail);
    }
}
