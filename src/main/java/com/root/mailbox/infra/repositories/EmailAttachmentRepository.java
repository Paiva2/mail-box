package com.root.mailbox.infra.repositories;

import com.root.mailbox.domain.entities.EmailAttachment;
import com.root.mailbox.domain.entities.keys.EmailAttachmentKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailAttachmentRepository extends JpaRepository<EmailAttachment, EmailAttachmentKey> {
}
