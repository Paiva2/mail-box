package com.root.mailbox.presentation.dto.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ListTrashEmailsPaginationDTO {
    private Integer page;
    private Integer size;
    private String keyword;
    private Boolean opened;
    private Boolean spam;
}
