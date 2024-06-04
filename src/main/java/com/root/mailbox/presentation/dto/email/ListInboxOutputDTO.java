package com.root.mailbox.presentation.dto.email;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ListInboxOutputDTO {
    Integer page;
    Integer size;
    Long totalItems;
    List<InboxOutputDTO> emails;
}
