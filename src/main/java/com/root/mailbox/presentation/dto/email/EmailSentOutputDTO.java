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
public class EmailSentOutputDTO {
    private UUID id;
    private String subject;
    private String message;
    private Date createdAt;
    private Boolean hasOpeningOrder;
    private List<EmailOpeningOrderOutputDTO> openingOrders;
    private List<UserReceivingEmailOutputDTO> usersReceivingEmailOutput;
    private List<CarbonCopyOutputDTO> ccs;
    private List<AttachmentOutputDTO> attachments;
}
