package com.root.mailbox.presentation.dto.folder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ListFolderEmailsPaginationInputDTO {
    private Integer page;
    private Integer size;
    private String keyword;
}
