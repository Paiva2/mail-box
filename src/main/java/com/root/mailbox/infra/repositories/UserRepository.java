package com.root.mailbox.infra.repositories;

import com.root.mailbox.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email = :email AND (u.disabled = false OR u.disabled = NULL)")
    Optional<User> findByEmailAndEnabled(@Param("email") String email);


    @Query("SELECT u FROM User u WHERE u.recoverEmail = :email AND (u.disabled = false OR u.disabled = NULL)")
    Optional<User> findByRecoverEmailAndEnabled(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.id = :id AND (u.disabled = false OR u.disabled = NULL)")
    Optional<User> findByIdEnabled(@Param("id") Long id);
}
