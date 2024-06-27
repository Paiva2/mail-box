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
    @Query(nativeQuery = true, value = "SELECT * FROM tb_emails e " +
        "JOIN tb_users u ON u.USR_ID = e.EM_USER_ID " +
        "WHERE u.USR_ID = :userId " +
        "AND ( :keyword IS NULL OR LOWER(e.EM_SUBJECT) LIKE CONCAT('%', LOWER(:keyword), '%') ) " +
        "AND e.EM_EMAIL_STATUS <> 'DRAFT' " +
        "AND e.EM_DISABLED IS FALSE " +
        "AND e.EM_DISABLED_AT IS NULL")
    Page<Email> findAllByUserFiltering(@Param("userId") Long userId, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT e FROM Email e " +
        "LEFT JOIN e.usersEmails ue " +
        "LEFT JOIN e.emailOpeningOrders eo " +
        "INNER JOIN e.user u " +
        "WHERE e.id = :emailId AND u.id = :userId " +
        "AND (e.disabled = false AND e.deletedAt = null)")
    Optional<Email> findByIdAndUser(@Param("emailId") UUID emailId, @Param("userId") Long userId);

    @Query(nativeQuery = true, value = """
        SELECT * FROM tb_emails em
        WHERE em.EM_USER_ID = :userId
        AND (em.EM_DISABLED IS FALSE AND em.EM_DISABLED_AT IS NULL)
        AND em.EM_EMAIL_STATUS = 'DRAFT'
        AND ( :keyword IS NULL OR LOWER(em.EM_SUBJECT) LIKE CONCAT('%', LOWER(:keyword), '%') )
        """)
    Page<Email> findAllDraftsByUser(@Param("userId") Long userId, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT e FROM Email e " +
        "WHERE e.id = :emailId " +
        "AND e.emailStatus = 'DRAFT' " +
        "AND e.disabled = false " +
        "AND e.deletedAt = null")
    Optional<Email> findDraftById(@Param("emailId") UUID emailId);
}
