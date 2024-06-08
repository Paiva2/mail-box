package com.root.mailbox.infra.repositories;

import com.root.mailbox.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.id = :id AND (u.disabled = false OR u.disabled = NULL)")
    Optional<User> findByIdEnabled(@Param("id") Long id);

    @Query("SELECT u FROM User u WHERE u.email IN (:emails) AND (u.disabled = false OR u.disabled = NULL)")
    List<User> findAllByEmail(@Param("emails") List<String> emails);
}
