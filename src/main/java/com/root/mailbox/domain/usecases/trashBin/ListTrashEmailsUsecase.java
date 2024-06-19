package com.root.mailbox.domain.usecases.trashBin;

import com.root.mailbox.domain.entities.TrashBin;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.entities.UserEmail;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.infra.providers.TrashBinDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.infra.providers.UserEmailDataProvider;
import com.root.mailbox.presentation.dto.email.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ListTrashEmailsUsecase {
    private final UserDataProvider userDataProvider;
    private final UserEmailDataProvider userEmailDataProvider;
    private final TrashBinDataProvider trashBinDataProvider;

    public ListTrashEmailsOutputDTO exec(Long userId, ListTrashEmailsPaginationDTO dto) {
        User user = checkIfUserExists(userId);

        if (user.getDisabled()) {
            throw new UserDisabledException(user.getId());
        }

        handleDefaultPagination(dto);

        TrashBin trashBin = getUserTrashBin(user.getId());

        Page<UserEmail> listEmailsOnTrash = getTrashEmails(userId, dto, trashBin);

        return mountOutput(listEmailsOnTrash, dto);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private void handleDefaultPagination(ListTrashEmailsPaginationDTO dto) {
        if (dto.getPage() < 1) {
            dto.setPage(1);
        }

        if (dto.getSize() < 5) {
            dto.setSize(5);
        } else if (dto.getSize() > 50) {
            dto.setSize(50);
        }
    }

    private TrashBin getUserTrashBin(Long userId) {
        return trashBinDataProvider.findByUser(userId).orElseThrow(() -> new RuntimeException("Error while getting User TrashBin..."));
    }

    private Page<UserEmail> getTrashEmails(Long userId, ListTrashEmailsPaginationDTO dto, TrashBin trashBin) {
        Pageable pageable = PageRequest.of(dto.getPage() - 1, dto.getSize(), Sort.Direction.DESC, "UM_CREATED_AT");

        return userEmailDataProvider.findAllUserEmailsOnTrash(userId, trashBin.getId(), dto.getKeyword(), dto.getOpened(), dto.getSpam(), pageable);
    }

    private ListTrashEmailsOutputDTO mountOutput(Page<UserEmail> userEmails, ListTrashEmailsPaginationDTO dto) {
        return ListTrashEmailsOutputDTO.builder()
            .page(userEmails.getNumber() + 1)
            .size(userEmails.getSize())
            .totalItems(userEmails.getTotalElements())
            .keyword(dto.getKeyword())
            .spam(dto.getSpam())
            .opened(dto.getOpened())
            .emails(userEmails.getContent().stream().map(
                userEmail -> EmailOutputDTO.builder()
                    .id(userEmail.getEmail().getId())
                    .subject(userEmail.getEmail().getSubject())
                    .message(userEmail.getEmail().getMessage())
                    .opened(userEmail.getOpened())
                    .isSpam(userEmail.getIsSpam())
                    .hasOrder(userEmail.getEmail().getOpeningOrders())
                    .createdAt(userEmail.getCreatedAt())
                    .build()
            ).toList())
            .build();
    }
}
