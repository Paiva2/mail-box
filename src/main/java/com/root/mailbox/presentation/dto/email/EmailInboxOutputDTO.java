package com.root.mailbox.presentation.dto.email;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EmailInboxOutputDTO {
    Integer page;
    Integer size;
    Long totalItems;
    Integer totalPages;
    List<InboxOutputDTO> emails;
}
