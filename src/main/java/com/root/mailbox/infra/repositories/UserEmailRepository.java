package com.root.mailbox.infra.repositories;

import com.root.mailbox.domain.entities.UserEmail;
import com.root.mailbox.domain.entities.keys.UserEmailKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserEmailRepository extends JpaRepository<UserEmail, UserEmailKey> {
    @Query("SELECT ue FROM UserEmail ue " +
        "JOIN FETCH ue.user u " +
        "JOIN FETCH ue.email e " +
        "WHERE e.id = :emailId " +
        "AND e.userTo.id = :userId")
    Optional<UserEmail> findByUserAndIdAndUserTo(@Param("userId") Long userId, @Param("emailId") UUID emailId);

    @Query(nativeQuery = true, value = "SELECT * FROM tb_users_emails ue " +
        "JOIN tb_users u ON ue.UM_USER_ID = u.USR_ID " +
        "JOIN tb_emails e ON e.EM_ID = ue.UM_EMAIL_ID " +
        "WHERE ue.UM_USER_ID = :userId " +
        "AND (e.EM_DISABLED = false OR e.EM_DISABLED IS NULL) " +
        "AND (u.USR_DISABLED = false OR u.USR_DISABLED IS NULL) " +
        "AND (:keyword IS NULL OR LOWER(e.EM_SUBJECT) LIKE CONCAT('%', LOWER(:keyword), '%'))")
    Page<UserEmail> findAllByUserId(@Param("userId") Long userId, @Param("keyword") String keyword, Pageable pageable);
}
