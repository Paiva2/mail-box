package com.root.mailbox.infra.repositories;

import com.root.mailbox.domain.entities.UserEmail;
import com.root.mailbox.domain.entities.keys.UserEmailKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserEmailRepository extends JpaRepository<UserEmail, UserEmailKey> {
}
