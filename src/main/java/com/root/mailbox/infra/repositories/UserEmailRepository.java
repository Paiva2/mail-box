package com.root.mailbox.infra.repositories;

import com.root.mailbox.domain.entities.UserEmail;
import com.root.mailbox.domain.entities.keys.UserEmailKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserEmailRepository extends JpaRepository<UserEmail, UserEmailKey> {
    @Query(nativeQuery = true, value = "SELECT * FROM tb_users_emails ue " +
        "JOIN tb_users u ON ue.UM_USER_ID = u.USR_ID " +
        "JOIN tb_emails e ON e.EM_ID = ue.UM_EMAIL_ID " +
        "WHERE ue.UM_USER_ID = :userId " +
        "AND (ue.UM_DISABLED IS FALSE AND ue.UM_DELETED_AT IS NULL) " +
        "AND (ue.UM_EMAIL_TYPE = 'RECEIVED' OR ue.UM_EMAIL_TYPE = 'IN_COPY') " +
        "AND (u.USR_DISABLED IS FALSE AND u.USR_DISABLED_AT IS NULL) " +
        "AND (:filteringSpam IS NULL OR ue.UM_IS_SPAM = :filteringSpam) " +
        "AND (:opened IS NULL OR ue.UM_OPENED = :opened) " +
        "AND ( :keyword IS NULL OR LOWER(e.EM_SUBJECT) LIKE CONCAT('%', LOWER(:keyword), '%') )")
    Page<UserEmail> findAllByUserIdFilter(
        @Param("userId") Long userId,
        @Param("keyword") String keyword,
        @Param("filteringSpam") Boolean filteringSpam,
        @Param("opened") Boolean opened,
        Pageable pageable
    );

    @Query("SELECT ue FROM UserEmail ue " +
        "JOIN FETCH ue.user u " +
        "JOIN FETCH ue.email e " +
        "WHERE e.id = :emailId " +
        "AND (ue.emailType = 'RECEIVED' OR ue.emailType = 'IN_COPY') " +
        "AND u.id = :userId " +
        "AND ue.deletedAt = null")
    Optional<UserEmail> findByUserIdAndEmailId(@Param("userId") Long userId, @Param("emailId") UUID emailId);

    @Modifying
    @Query("UPDATE UserEmail ue SET ue.opened = true WHERE ue.email.id = :emailId AND ue.user.id = :userId")
    void markOpened(@Param("userId") Long userId, @Param("emailId") UUID emailId);
}
