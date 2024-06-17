package com.root.mailbox.infra.repositories;

import com.root.mailbox.domain.entities.TrashBin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrashBinRepository extends JpaRepository<TrashBin, UUID> {
    Optional<TrashBin> findByUserId(Long userId);
}
