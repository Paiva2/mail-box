package com.root.mailbox.infra.providers;

import com.root.mailbox.infra.repositories.AnswerAttachmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AnswerAttachmentDataProvider {
    private final AnswerAttachmentRepository answerAttachmentRepository;
}
