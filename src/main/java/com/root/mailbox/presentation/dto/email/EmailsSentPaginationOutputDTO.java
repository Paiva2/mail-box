package com.root.mailbox.presentation.dto.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EmailsSentPaginationOutputDTO {
    private Integer page;
    private Integer size;
    private Long totalItems;
    private List<EmailSentOutputDTO> emails;
}
