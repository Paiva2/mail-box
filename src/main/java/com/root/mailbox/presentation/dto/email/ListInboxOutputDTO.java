package com.root.mailbox.presentation.dto.email;

import com.root.mailbox.domain.entities.Email;
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
    List<EmailOutputDTO> emails;
}
