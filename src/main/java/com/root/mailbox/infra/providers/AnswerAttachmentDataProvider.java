package com.root.mailbox.infra.providers;

import com.root.mailbox.domain.entities.AnswerAttachment;
import com.root.mailbox.infra.repositories.AnswerAttachmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class AnswerAttachmentDataProvider {
    private final AnswerAttachmentRepository answerAttachmentRepository;

    public AnswerAttachment save(AnswerAttachment answerAttachment) {
        return answerAttachmentRepository.save(answerAttachment);
    }

    public Optional<AnswerAttachment> findByAnswerAndAttachment(UUID answerId, UUID attachmentId) {
        return answerAttachmentRepository.findByAnswerIdAndAttachmentId(answerId, attachmentId);
    }
}
