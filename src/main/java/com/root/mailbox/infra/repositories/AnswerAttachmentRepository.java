package com.root.mailbox.infra.repositories;

import com.root.mailbox.domain.entities.AnswerAttachment;
import com.root.mailbox.domain.entities.keys.AnswerAttachmentKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerAttachmentRepository extends JpaRepository<AnswerAttachment, AnswerAttachmentKey> {
}
