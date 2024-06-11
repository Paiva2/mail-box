package com.root.mailbox.presentation.dto.email;

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
    List<CarbonCopyOutputDTO> ccs;
}
