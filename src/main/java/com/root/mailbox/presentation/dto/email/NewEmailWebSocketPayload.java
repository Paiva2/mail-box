package com.root.mailbox.presentation.dto.email;

import com.root.mailbox.domain.entities.Attachment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NewEmailWebSocketPayload {
    private UUID id;
    private Boolean hasOrder;
    private Boolean opened;
    private String message;
    private String title;
    private Date createdAt;
    private String from;
    private String fromName;
    private String fromProfilePicture;
    private LinkedHashSet<String> usersReceiving;
    private List<String> copies;
    private List<Attachment> attachments;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    static class Attachment {
        private UUID id;
        private String url;
        private String fileName;
        private Date createdAt;
        private com.root.mailbox.domain.entities.Attachment.FileExtension extension;
    }
}
