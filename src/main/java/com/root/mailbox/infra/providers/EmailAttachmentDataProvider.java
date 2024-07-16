package com.root.mailbox.infra.providers;

import com.root.mailbox.domain.entities.EmailAttachment;
import com.root.mailbox.infra.repositories.EmailAttachmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class EmailAttachmentDataProvider {
    private final EmailAttachmentRepository emailAttachmentRepository;

    public EmailAttachment save(EmailAttachment emailAttachment) {
        return emailAttachmentRepository.save(emailAttachment);
    }

    public Optional<EmailAttachment> findByEmailAndAttachment(UUID emailId, UUID attachmentId) {
        return emailAttachmentRepository.findByEmailIdAndAttachmentId(emailId, attachmentId);
    }
}
