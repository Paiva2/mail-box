package com.root.mailbox.infra.repositories;

import com.root.mailbox.domain.entities.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, UUID> {
    Optional<Answer> findByIdAndUserId(@Param("answerId") UUID answerId, @Param("userId") Long userId);
}
