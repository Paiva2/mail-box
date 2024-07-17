package com.root.mailbox.infra.repositories;

import com.root.mailbox.domain.entities.AnswerAttachment;
import com.root.mailbox.domain.entities.keys.AnswerAttachmentKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnswerAttachmentRepository extends JpaRepository<AnswerAttachment, AnswerAttachmentKey> {
    Optional<AnswerAttachment> findByAnswerIdAndAttachmentId(@Param("answerId") UUID answerId, @Param("attachmentId") UUID attachmentId);
}
