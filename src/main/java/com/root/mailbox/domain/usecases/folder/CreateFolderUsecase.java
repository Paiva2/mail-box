package com.root.mailbox.domain.usecases.folder;

import com.root.mailbox.domain.entities.Folder;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.exceptions.folder.ParentFolderNotFoundException;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.infra.providers.FolderDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.presentation.dto.folder.FolderOutputDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@AllArgsConstructor
@Service
public class CreateFolderUsecase {
    private final UserDataProvider userDataProvider;
    private final FolderDataProvider folderDataProvider;

    public FolderOutputDTO exec(Long userId, Folder newFolder) {
        User user = checkIfUserExists(userId);

        if (user.getDisabled()) {
            throw new UserDisabledException(user.getId());
        }

        newFolder.setUser(user);

        String folderName = newFolder.getName().trim();
        Integer nameCount = 1;

        while (true) {
            Folder folderExistentName = checkIfUserContainFolderName(user.getId(), folderName);

            if (Objects.isNull(folderExistentName) || folderExistentName.getDisabled()) {
                newFolder.setName(folderName);
                break;
            }

            folderName = newFolder.getName().trim().concat(" (").concat(nameCount.toString()).concat(")");
            nameCount++;
        }

        if (Objects.nonNull(newFolder.getParentFolder())) {
            Folder parentFolder = checkIfParentFolderExists(newFolder.getParentFolder().getId());

            if (parentFolder.getDisabled()) {
                throw new ParentFolderNotFoundException(parentFolder.getId());
            }

            newFolder.setParentFolder(parentFolder);
        }

        Folder folderSaved = persistNewFolder(newFolder);

        return mountOutput(folderSaved);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private Folder checkIfUserContainFolderName(Long userId, String folderName) {
        return folderDataProvider.findByUserAndName(userId, folderName).orElse(null);
    }

    private Folder checkIfParentFolderExists(Long folderId) {
        return folderDataProvider.findById(folderId).orElseThrow(() -> new ParentFolderNotFoundException(folderId));
    }

    private Folder persistNewFolder(Folder folder) {
        folder.setDisabled(false);

        return folderDataProvider.save(folder);
    }

    private FolderOutputDTO mountOutput(Folder folder) {
        return FolderOutputDTO.builder()
            .id(folder.getId())
            .name(folder.getName())
            .createdAt(folder.getCreatedAt())
            .disabled(folder.getDisabled())
            .parentFolder(Objects.nonNull(folder.getParentFolder()) ? FolderOutputDTO.builder()
                .id(folder.getParentFolder().getId())
                .name(folder.getParentFolder().getName())
                .createdAt(folder.getParentFolder().getCreatedAt())
                .disabled(folder.getParentFolder().getDisabled())
                .build() : null)
            .build();
    }
}
