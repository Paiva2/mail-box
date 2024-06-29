package com.root.mailbox.domain.usecases.email;

import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.entities.UserEmail;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.infra.providers.UserEmailDataProvider;
import com.root.mailbox.presentation.dto.email.DraftEmailsOutputDTO;
import com.root.mailbox.presentation.dto.email.ListDraftEmailsOutputDTO;
import com.root.mailbox.presentation.dto.email.ListDraftEmailsPaginationInputDTO;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ListDraftEmailsUsecase {
    private final UserDataProvider userDataProvider;
    private final UserEmailDataProvider userEmailDataProvider;

    public ListDraftEmailsOutputDTO exec(Long userId, ListDraftEmailsPaginationInputDTO dto) {
        User user = checkIfUserExists(userId);

        if (user.getDisabled()) {
            throw new UserDisabledException(user.getId());
        }

        handleDefaultPagination(dto);

        Page<UserEmail> draftEmails = listEmailsDraft(user.getId(), dto);

        return mountOutput(draftEmails, dto.getKeyword());
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private void handleDefaultPagination(ListDraftEmailsPaginationInputDTO dto) {
        if (dto.getPage() < 1) {
            dto.setPage(1);
        }

        if (dto.getSize() < 5) {
            dto.setSize(5);
        } else if (dto.getSize() > 50) {
            dto.setSize(50);
        }
    }

    private Page<UserEmail> listEmailsDraft(Long userId, ListDraftEmailsPaginationInputDTO dto) {
        Pageable pageable = PageRequest.of(dto.getPage() - 1, dto.getSize(), Sort.Direction.DESC, "UM_CREATED_AT");

        return userEmailDataProvider.findAllDraftByUser(userId, dto.getKeyword(), pageable);
    }

    private ListDraftEmailsOutputDTO mountOutput(Page<UserEmail> emails, String keyword) {
        return ListDraftEmailsOutputDTO.builder()
            .page(emails.getNumber() + 1)
            .size(emails.getSize())
            .totalItems(emails.getTotalElements())
            .keyword(keyword)
            .emails(emails.getContent().stream().map(userEmail -> DraftEmailsOutputDTO.builder()
                .id(userEmail.getEmail().getId())
                .subject(userEmail.getEmail().getSubject())
                .message(userEmail.getEmail().getMessage())
                .status(userEmail.getEmail().getEmailStatus())
                .createdAt(userEmail.getEmail().getCreatedAt())
                .build()
            ).toList())
            .build();
    }
}
