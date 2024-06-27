package com.root.mailbox.domain.usecases.folder;

import com.root.mailbox.domain.entities.Folder;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.entities.dto.FolderDTO;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.infra.providers.FolderDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.presentation.dto.folder.FolderOutputDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Service
public class ListAllRootFoldersUsecase {
    private final UserDataProvider userDataProvider;
    private final FolderDataProvider folderDataProvider;

    public List<FolderOutputDTO> exec(Long userId) {
        User user = checkIfUserExists(userId);

        if (user.getDisabled()) {
            throw new UserDisabledException(user.getId());
        }

        List<FolderDTO> userFolders = findAllUserFolders(user.getId());

        return mountOutput(userFolders);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private List<FolderDTO> findAllUserFolders(Long userId) {
        return folderDataProvider.findAllRootByUser(userId);
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
