package com.root.mailbox.presentation.dto.email;

import com.root.mailbox.presentation.dto.attachment.AttachmentOutputDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class InboxOutputDTO {
    private UUID id;
    private String subject;
    private String message;
    private Boolean opened;
    private Boolean isSpam;
    private Boolean hasOrder;
    private String emailOwnerName;
    private String emailOwnerPicture;
    private List<AttachmentOutputDTO> attachment;
    private Date createdAt;
}
