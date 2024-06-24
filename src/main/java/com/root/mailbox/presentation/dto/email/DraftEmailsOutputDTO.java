package com.root.mailbox.presentation.dto.email;

import com.root.mailbox.domain.entities.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DraftEmailsOutputDTO {
    private String subject;
    private String message;
    private Date createdAt;
    private Email.EmailStatus status;
}
