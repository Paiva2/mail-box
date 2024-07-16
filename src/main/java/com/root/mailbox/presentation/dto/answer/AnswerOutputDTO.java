package com.root.mailbox.presentation.dto.answer;

import com.root.mailbox.presentation.dto.attachment.AttachmentOutputDTO;
import com.root.mailbox.presentation.dto.user.GetUserProfileOutputDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AnswerOutputDTO {
    private String message;
    private GetUserProfileOutputDTO userAnswering;
    private Date createdAt;
    private List<AttachmentOutputDTO> attachments;
}
