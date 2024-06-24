package com.root.mailbox.presentation.dto.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ListDraftEmailsPaginationInputDTO {
    private Integer page;
    private Integer size;
    private String keyword;
}
