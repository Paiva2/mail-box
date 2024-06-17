package com.root.mailbox.infra.providers;

import com.root.mailbox.domain.entities.TrashBin;
import com.root.mailbox.infra.repositories.TrashBinRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class TrashBinDataProvider {
    private final TrashBinRepository trashBinRepository;

    public TrashBin create(TrashBin trashBin) {
        return trashBinRepository.save(trashBin);
    }

    public Optional<TrashBin> findByUser(Long userId) {
        return trashBinRepository.findByUserId(userId);
    }
}
