package com.root.mailbox.domain.usecases.folder;

import com.root.mailbox.domain.entities.Folder;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.entities.UserEmail;
import com.root.mailbox.domain.exceptions.folder.FolderNotFoundException;
import com.root.mailbox.domain.exceptions.generic.ForbiddenException;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.infra.providers.FolderDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.infra.providers.UserEmailDataProvider;
import com.root.mailbox.presentation.dto.email.EmailInboxOutputDTO;
import com.root.mailbox.presentation.dto.email.InboxOutputDTO;
import com.root.mailbox.presentation.dto.folder.ListFolderEmailsPaginationInputDTO;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ListFolderEmailsUsecase {
    private final UserDataProvider userDataProvider;
    private final FolderDataProvider folderDataProvider;
    private final UserEmailDataProvider userEmailDataProvider;

    public EmailInboxOutputDTO exec(Long userId, Long folderId, ListFolderEmailsPaginationInputDTO dto) {
        User user = checkIfUserExists(userId);

        if (user.getDisabled()) {
            throw new UserDisabledException(user.getId());
        }

        Folder folder = checkIfFolderExists(folderId);

        if (folder.getDisabled()) {
            throw new FolderNotFoundException(folder.getId());
        }

        checkPermissions(folder, user);

        handleDefaultPagination(dto);

        Page<UserEmail> folderEmails = getAllFolderEmails(user.getId(), folder.getId(), dto);

        return mountOutput(folderEmails);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private Folder checkIfFolderExists(Long parentFolderId) {
        return folderDataProvider.findById(parentFolderId).orElseThrow(() -> new FolderNotFoundException(parentFolderId));
    }

    private void checkPermissions(Folder folder, User user) {
        Long userId = user.getId();
        Long folderUserId = folder.getUser().getId();

        if (!folderUserId.equals(userId)) {
            throw new ForbiddenException();
        }
    }

    private void handleDefaultPagination(ListFolderEmailsPaginationInputDTO dto) {
        if (dto.getPage() < 1) {
            dto.setPage(1);
        }

        if (dto.getSize() < 5) {
            dto.setSize(5);
        } else if (dto.getSize() > 50) {
            dto.setSize(50);
        }
    }

    private Page<UserEmail> getAllFolderEmails(Long userId, Long folderId, ListFolderEmailsPaginationInputDTO dto) {
        Pageable pageable = PageRequest.of(dto.getPage() - 1, dto.getSize(), Sort.Direction.DESC, "UM_CREATED_AT");

        return userEmailDataProvider.findAllByUserAndFolder(userId, folderId, pageable, dto.getKeyword());
    }

    private EmailInboxOutputDTO mountOutput(Page<UserEmail> userEmails) {
        return EmailInboxOutputDTO.builder()
            .page(userEmails.getNumber() + 1)
            .size(userEmails.getSize())
            .totalItems(userEmails.getTotalElements())
            .emails(userEmails.getContent().stream().map(userEmail -> InboxOutputDTO.builder()
                    .id(userEmail.getEmail().getId())
                    .message(userEmail.getEmail().getMessage())
                    .subject(userEmail.getEmail().getSubject())
                    .createdAt(userEmail.getCreatedAt())
                    .isSpam(userEmail.getIsSpam())
                    .opened(userEmail.getOpened())
                    .hasOrder(userEmail.getEmail().getOpeningOrders())
                    .build())
                .toList()
            ).build();
    }
}
