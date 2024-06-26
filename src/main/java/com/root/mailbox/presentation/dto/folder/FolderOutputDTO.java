package com.root.mailbox.presentation.dto.folder;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FolderOutputDTO {
    private Long id;
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private FolderOutputDTO parentFolder;
    private Boolean disabled = false;
    private Date createdAt;
}
