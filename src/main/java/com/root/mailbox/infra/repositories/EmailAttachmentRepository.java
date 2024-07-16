package com.root.mailbox.infra.repositories;

import com.root.mailbox.domain.entities.EmailAttachment;
import com.root.mailbox.domain.entities.keys.EmailAttachmentKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailAttachmentRepository extends JpaRepository<EmailAttachment, EmailAttachmentKey> {
    @Query("""
        SELECT ea FROM EmailAttachment ea
        JOIN FETCH ea.email em
        JOIN FETCH ea.attachment at
        WHERE em.id = :emailId
        AND at.id = :attachmentId
        AND (em.disabled = false AND em.deletedAt = null)
        """)
    Optional<EmailAttachment> findByEmailIdAndAttachmentId(@Param("emailId") UUID emailId, @Param("attachmentId") UUID attachmentId);
}
