package com.root.mailbox.infra.repositories;

import com.root.mailbox.domain.entities.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailRepository extends JpaRepository<Email, UUID> {
    @Query("SELECT e FROM Email e " +
        "LEFT JOIN e.usersEmails ue " +
        "LEFT JOIN e.emailOpeningOrders eo " +
        "INNER JOIN e.user u " +
        "WHERE e.id = :emailId AND u.id = :userId " +
        "AND (e.disabled = false AND e.deletedAt = null)")
    Optional<Email> findByIdAndUser(@Param("emailId") UUID emailId, @Param("userId") Long userId);

    @Query("SELECT e FROM Email e " +
        "WHERE e.id = :emailId " +
        "AND e.emailStatus = 'DRAFT' " +
        "AND e.disabled = false " +
        "AND e.deletedAt = null")
    Optional<Email> findDraftById(@Param("emailId") UUID emailId);
}
