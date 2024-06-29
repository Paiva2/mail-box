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
import java.util.Optional;

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

        String originalFolderName = newFolder.getName().trim();

        if (Objects.isNull(newFolder.getParentFolder())) {
            handleFolderName(newFolder, user.getId(), originalFolderName, false, null);
        } else {
            Folder parentFolder = checkIfParentFolderExists(newFolder.getParentFolder().getId());

            if (parentFolder.getDisabled()) {
                throw new ParentFolderNotFoundException(parentFolder.getId());
            }

            newFolder.setParentFolder(parentFolder);
            Optional<Folder> parentChildFolder = checkIfParentFolderHasChildrenWithName(userId, newFolder.getName(), parentFolder.getId());

            if (parentChildFolder.isPresent()) {
                handleFolderName(newFolder, user.getId(), originalFolderName, true, parentFolder.getId());
            }
        }

        Folder folderSaved = persistNewFolder(newFolder);

        return mountOutput(folderSaved);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private Optional<Folder> checkIfUserContainFolderNameOnRoot(Long userId, String folderName) {
        return folderDataProvider.findByUserAndNameInRoot(userId, folderName);
    }

    private Optional<Folder> checkIfParentFolderHasChildrenWithName(Long userId, String folderName, Long parentFolderId) {
        return folderDataProvider.findByUserAndNameInParent(userId, folderName, parentFolderId);
    }

    private Folder checkIfParentFolderExists(Long folderId) {
        return folderDataProvider.findById(folderId).orElseThrow(() -> new ParentFolderNotFoundException(folderId));
    }

    private void handleFolderName(Folder newFolder, Long userId, String originalFolderName, Boolean parentSearch, Long parentFolderId) {
        Integer nameCount = 1;

        while (true) {
            Optional<Folder> findFolder;

            if (parentSearch && Objects.nonNull(parentFolderId)) {
                findFolder = checkIfParentFolderHasChildrenWithName(userId, newFolder.getName(), parentFolderId);
            } else {
                findFolder = checkIfUserContainFolderNameOnRoot(userId, newFolder.getName());
            }

            if (findFolder.isEmpty() || findFolder.get().getDisabled()) break;

            String newName = generateFolderNameCounting(nameCount, originalFolderName);
            newFolder.setName(newName);

            nameCount++;
        }
    }

    private String generateFolderNameCounting(Integer nameCount, String originalName) {
        StringBuilder name = new StringBuilder(originalName);
        name.append(" (").append(nameCount).append(")");

        return name.toString();
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
