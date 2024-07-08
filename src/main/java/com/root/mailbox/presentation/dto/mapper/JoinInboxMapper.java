package com.root.mailbox.presentation.dto.mapper;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class JoinInboxMapper {
    private Long id;
    private String email;
    private INBOX_TYPE messageType;

    public enum INBOX_TYPE {
        NEW_CONNECTION_INBOX,
        DISCONNECTED_INBOX
    }
}
