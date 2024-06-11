package com.root.mailbox.domain.usecases.email;

import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.entities.UserEmail;
import com.root.mailbox.domain.exceptions.UserNotFoundException;
import com.root.mailbox.infra.providers.EmailDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.presentation.dto.email.InboxOutputDTO;
import com.root.mailbox.presentation.dto.email.InboxPaginationDTO;
import com.root.mailbox.presentation.dto.email.ListInboxOutputDTO;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GetInboxUsecase {
    private final UserDataProvider userDataProvider;
    private final EmailDataProvider emailDataProvider;

    public ListInboxOutputDTO exec(Long userId, InboxPaginationDTO dto) {
        handlePaginationDefault(dto);
        User user = checkIfUserExists(userId);

        return getInboxList(user, dto);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private void handlePaginationDefault(InboxPaginationDTO dto) {
        if (dto.getPage() < 1) {
            dto.setPage(1);
        }

        if (dto.getSize() < 5) {
            dto.setSize(5);
        } else if (dto.getSize() > 50) {
            dto.setSize(50);
        }
    }

    private ListInboxOutputDTO getInboxList(User user, InboxPaginationDTO dto) {
        Pageable pageable = PageRequest.of(dto.getPage() - 1, dto.getSize(), Sort.Direction.DESC, "UM_CREATED_AT");

        Page<UserEmail> userEmails = emailDataProvider.findAllUserEmailByUser(user.getId(), dto.getKeyword(), dto.getFilteringSpam(), pageable);

        return ListInboxOutputDTO.builder()
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
