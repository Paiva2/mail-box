package com.root.mailbox.presentation.dto.email;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class InboxPaginationDTO {
    private Integer page;
    private Integer size;
    private Long totalItems;
    private String keyword;
    private Boolean filteringSpam;
}
