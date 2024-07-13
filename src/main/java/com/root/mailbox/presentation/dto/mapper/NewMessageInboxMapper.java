package com.root.mailbox.presentation.dto.mapper;

import com.root.mailbox.presentation.dto.email.NewEmailWebSocketPayload;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class NewMessageInboxMapper {
    private NewEmailWebSocketPayload emailPayload;
    private MESSAGE_TYPE messageType;

    public enum MESSAGE_TYPE {
        NEW_EMAIL_INBOX
    }
}
