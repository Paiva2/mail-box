package com.root.mailbox.presentation.dto.email;

import com.root.mailbox.domain.entities.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DraftEmailsOutputDTO {
    private UUID id;
    private String subject;
    private String message;
    private Date createdAt;
    private Email.EmailStatus status;
}
