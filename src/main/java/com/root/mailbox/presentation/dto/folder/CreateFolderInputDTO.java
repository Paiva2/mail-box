package com.root.mailbox.presentation.dto.folder;

import com.root.mailbox.domain.entities.Folder;
import com.root.mailbox.domain.entities.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateFolderInputDTO {
    @NotBlank
    @Size(min = 3)
    private String name;
    private Long parentFolderId;

    public Folder toFolder() {
        return Folder.builder()
            .name(this.name)
            .parentFolder(Objects.isNull(this.parentFolderId) ? null : Folder.builder().id(this.parentFolderId).build())
            .build();
    }
}
