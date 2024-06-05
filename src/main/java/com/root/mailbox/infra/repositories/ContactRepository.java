package com.root.mailbox.infra.repositories;

import com.root.mailbox.domain.entities.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    Optional<Contact> findByNameAndUserId(String name, Long userId);


    @Query(nativeQuery = true, value = "SELECT * FROM tb_contacts c " +
        "JOIN tb_users u " +
        "ON u.USR_ID = c.CT_USER_ID " +
        "WHERE c.CT_USER_ID = :userId " +
        "AND (:name IS NULL OR LOWER(c.CT_NAME) LIKE CONCAT('%', LOWER(:name),'%'))")
    Page<Contact> findAllByUserId(@Param("userId") Long userId, @Param("name") String name, Pageable pageable);
}
