package com.root.mailbox.domain.usecases.folder;

import com.root.mailbox.domain.entities.Folder;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.entities.dto.FolderDTO;
import com.root.mailbox.domain.exceptions.folder.FolderNotFoundException;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.infra.providers.FolderDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.presentation.dto.folder.FolderOutputDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class FilterParentFoldersTreeUsecase {
    private final UserDataProvider userDataProvider;
    private final FolderDataProvider folderDataProvider;

    public List<FolderOutputDTO> exec(Long userId, Long parentFolderId) {
        User user = checkIfUserExists(userId);

        if (user.getDisabled()) {
            throw new UserDisabledException(user.getId());
        }

        Folder parentFolder = checkIfParentFolderExists(parentFolderId);

        if (parentFolder.getDisabled()) {
            throw new FolderNotFoundException(parentFolder.getId());
        }

        List<FolderDTO> childrenFolders = getAllFolderChildrens(user.getId(), parentFolder.getId());

        return mountOutput(childrenFolders);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private Folder checkIfParentFolderExists(Long parentFolderId) {
        return folderDataProvider.findById(parentFolderId).orElseThrow(() -> new FolderNotFoundException(parentFolderId));
    }

    private List<FolderDTO> getAllFolderChildrens(Long userId, Long parentFolderId) {
        return folderDataProvider.findAllChildrenByFolderAndUser(userId, parentFolderId);
    }

    private List<FolderOutputDTO> mountOutput(List<FolderDTO> folders) {
        return folders.stream().map(folder ->
            FolderOutputDTO.builder()
                .id(folder.getId())
                .name(folder.getName())
                .disabled(folder.getDisabled())
                .createdAt(folder.getCreatedAt())
                .hasChildren(folder.getHasChildren())
                .build()
        ).toList();
    }
}
