package com.root.mailbox.infra.providers;

import com.root.mailbox.domain.entities.UserEmail;
import com.root.mailbox.infra.repositories.UserEmailRepository;
import com.root.mailbox.presentation.dto.email.ListTrashEmailsPaginationDTO;
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

    public List<UserEmail> saveAll(List<UserEmail> userEmail) {
        return userEmailRepository.saveAll(userEmail);
    }

    public UserEmail save(UserEmail userEmail) {
        return userEmailRepository.save(userEmail);
    }

    public Optional<UserEmail> findUserEmailAsReceiver(Long userId, UUID emailId) {
        return userEmailRepository.findByUserIdAndEmailId(userId, emailId);
    }

    public void markAsOpened(Long userId, UUID emailId) {
        userEmailRepository.markOpened(userId, emailId);
    }

    public Page<UserEmail> findAllReceivedByUser(Long userId, String keyword, Boolean filteringSpam, Boolean opened, UserEmail.EmailFlag flag, Pageable pageable) {
        return userEmailRepository.findAllReceivedByUserIdFilter(userId, keyword, filteringSpam, opened, flag, pageable);
    }

    public Optional<UserEmail> findUserEmail(Long userId, UUID emailId) {
        return userEmailRepository.findByUserIdAndEmailId(userId, emailId);
    }

    public UserEmail handleUserEmailSpam(UserEmail userEmail) {
        return userEmailRepository.save(userEmail);
    }

    public Page<UserEmail> findAllUserEmailsOnTrash(Long userId, UUID trashId, String keyword, Boolean opened, Boolean spam, Pageable pageable) {
        return userEmailRepository.findAllInTrashByUser(userId, trashId, keyword, opened, spam, pageable);
    }

    public Page<UserEmail> findAllByUserAndFolder(Long userId, Long folderId, Pageable pageable, String keyword) {
        return userEmailRepository.findAllByUserIdAndFolderId(userId, folderId, pageable, keyword);
    }

    public Page<UserEmail> findAllDraftByUser(Long userId, String keyword, Pageable pageable) {
        return userEmailRepository.findAllDraftsByUser(userId, keyword, pageable);
    }
}
