package com.root.mailbox.infra.repositories;

import com.root.mailbox.domain.entities.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmailRepository extends JpaRepository<Email, UUID> {
    @Query(nativeQuery = true, value = "SELECT * FROM tb_emails e " +
        "JOIN tb_users ut ON ut.USR_ID = e.EM_USER_ID " +
        "WHERE ut.USR_ID = :userId " +
        "AND ( :keyword = NULL OR LOWER(e.EM_SUBJECT) LIKE CONCAT('%', LOWER(:keyword), '%') ) " +
        "AND e.EM_DISABLED = false")
    Page<Email> findAllByUserFiltering(@Param("userId") Long userId, @Param("keyword") String keyword, Pageable pageable);
}
