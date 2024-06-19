package com.root.mailbox.infra.repositories;

import com.root.mailbox.domain.entities.TrashBinUserEmail;
import com.root.mailbox.domain.entities.keys.TrashBinUserEmailKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrashBinUserEmailRepository extends JpaRepository<TrashBinUserEmail, TrashBinUserEmailKey> {
    @Query("""
        SELECT tbu FROM TrashBinUserEmail tbu
        JOIN FETCH tbu.user u
        JOIN FETCH tbu.trashBin tb
        JOIN FETCH tbu.email em
        WHERE u.id = :userId
        AND em.id = :emailId
        AND tb.id = :trashBinId
        """)
    Optional<TrashBinUserEmail> findByUserIdAndEmailIdAndTrashBinId(@Param("userId") Long userId, @Param("emailId") UUID emailId, @Param("trashBinId") UUID trashBinId);

    @Modifying
    void deleteByUserIdAndEmailIdAndTrashBinId(Long userId, UUID emailId, UUID trashBinId);
}
