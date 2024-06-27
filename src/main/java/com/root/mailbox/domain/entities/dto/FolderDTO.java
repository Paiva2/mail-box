package com.root.mailbox.domain.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@Data
public class FolderDTO {
    private Long id;
    private String name;
    private Boolean hasChildren;
    private Boolean disabled;
    private Date createdAt;

    public FolderDTO(Long id, String name, Boolean hasChildren, Boolean disabled, Date createdAt) {
        this.id = id;
        this.name = name;
        this.disabled = disabled;
        this.createdAt = createdAt;
        this.hasChildren = hasChildren;
    }
}
