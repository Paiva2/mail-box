package com.root.mailbox.infra.providers;

import com.root.mailbox.domain.entities.Attachment;
import com.root.mailbox.infra.repositories.AttachmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class AttachmentDataProvider {
    private final AttachmentRepository attachmentRepository;

    public List<Attachment> saveAll(List<Attachment> attachments) {
        return attachmentRepository.saveAll(attachments);
    }

    public Optional<Attachment> findById(UUID id) {
        return attachmentRepository.findById(id);
    }
}
