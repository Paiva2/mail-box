package com.root.mailbox.presentation.dto.email;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class EmailOutputDTO {
    private UUID id;
    private String subject;
    private String message;
    private Boolean opened;
    private Boolean isSpam;
    private Boolean hasOrder;
    private Date createdAt;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<UserReceivingEmailOutputDTO> userReceivingEmailOutput;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<CarbonCopyOutputDTO> ccs;
}
