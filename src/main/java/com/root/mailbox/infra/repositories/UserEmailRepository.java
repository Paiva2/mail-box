package com.root.mailbox.infra.repositories;

import com.root.mailbox.domain.entities.UserEmail;
import com.root.mailbox.domain.entities.keys.UserEmailKey;
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
}
