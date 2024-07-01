package com.root.mailbox.presentation.dto.attachment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AttachmentOutputDTO {
    private UUID id;
    private String url;
    private String fileName;
    private Date createdAt;
}
