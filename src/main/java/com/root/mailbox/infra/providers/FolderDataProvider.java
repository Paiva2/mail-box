package com.root.mailbox.infra.providers;

import com.root.mailbox.domain.entities.Folder;
import com.root.mailbox.domain.entities.dto.FolderDTO;
import com.root.mailbox.infra.repositories.FolderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class FolderDataProvider {
    private final FolderRepository folderRepository;

    public Optional<Folder> findByUserAndNameInRoot(Long userId, String name) {
        return folderRepository.findByUserAndNameInRoot(userId, name);
    }

    public Optional<Folder> findByUserAndNameInParent(Long userId, String name, Long parentFolderId) {
        return folderRepository.findByUserAndNameInParent(userId, name, parentFolderId);
    }

    public Optional<Folder> findById(Long folderId) {
        return folderRepository.findById(folderId);
    }

    public Folder save(Folder folder) {
        return folderRepository.save(folder);
    }

    public List<FolderDTO> findAllRootByUser(Long userId) {
        return folderRepository.findAllRootByUserId(userId);
    }

    public List<FolderDTO> findAllChildrenByFolderAndUser(Long userId, Long parentFolderId) {
        return folderRepository.findAllChildrenByFolderIdAndUserId(userId, parentFolderId);
    }
}
