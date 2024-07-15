package com.root.mailbox.infra.providers;

import com.root.mailbox.infra.repositories.EmailAttachmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EmailAttachmentDataProvider {
    private final EmailAttachmentRepository emailAttachmentRepository;
}
