package com.root.mailbox.presentation.dto.email;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.root.mailbox.domain.entities.Email;
import com.root.mailbox.presentation.dto.answer.AnswerOutputDTO;
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
public class EmailOutputDTO {
    private UUID id;
    private String subject;
    private String message;
    private String sendFromName;
    private String sendFrom;
    private String sendFromProfilePicture;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean opened;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isSpam;
    private Boolean hasOrder;
    private Date createdAt;
    private Email.EmailStatus emailStatus;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<UserReceivingEmailOutputDTO> usersReceivingEmailOutput;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<CarbonCopyOutputDTO> ccs;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<AttachmentOutputDTO> attachments;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<AnswerOutputDTO> answers;
}
