package com.root.mailbox.infra.providers;

import com.root.mailbox.domain.entities.TrashBinUserEmail;
import com.root.mailbox.infra.repositories.TrashBinUserEmailRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class TrashBinUserEmailDataProvider {
    private final TrashBinUserEmailRepository trashBinUserEmailRepository;

    public Optional<TrashBinUserEmail> findByUserTrashAndEmailId(Long userId, UUID emailId, UUID trashBinId) {
        return trashBinUserEmailRepository.findByUserIdAndEmailIdAndTrashBinId(userId, emailId, trashBinId);
    }

    public TrashBinUserEmail create(TrashBinUserEmail trashBinUserEmail) {
        return trashBinUserEmailRepository.save(trashBinUserEmail);
    }

    public void delete(Long userId, UUID emailId, UUID trashBinId) {
        trashBinUserEmailRepository.deleteByUserIdAndEmailIdAndTrashBinId(userId, emailId, trashBinId);
    }
}
