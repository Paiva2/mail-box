package com.root.mailbox.domain.usecases.email;

import com.root.mailbox.domain.entities.Email;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.exceptions.UserNotFoundException;
import com.root.mailbox.infra.providers.EmailDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.presentation.dto.email.CarbonCopyOutputDTO;
import com.root.mailbox.presentation.dto.email.EmailSentOutputDTO;
import com.root.mailbox.presentation.dto.email.EmailsSentPaginationOutputDTO;
import com.root.mailbox.presentation.dto.email.EmailsSentPaginationInputDTO;
import com.root.mailbox.presentation.dto.user.GetUserProfileOutputDTO;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ListEmailsSentUsecase {
    private UserDataProvider userDataProvider;
    private EmailDataProvider emailDataProvider;

    public EmailsSentPaginationOutputDTO exec(Long userId, EmailsSentPaginationInputDTO dto) {
        User user = checkIfUserExists(userId);
        handlePagination(dto);

        Page<Email> sentEmails = getSentEmails(user.getId(), dto);

        return mountOutput(sentEmails);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private void handlePagination(EmailsSentPaginationInputDTO dto) {
        if (dto.getPage() < 1) {
            dto.setPage(1);
        }

        if (dto.getSize() < 5) {
            dto.setSize(5);
        } else if (dto.getSize() > 50) {
            dto.setSize(50);
        }
    }

    private Page<Email> getSentEmails(Long userId, EmailsSentPaginationInputDTO dto) {
        Pageable pageable = PageRequest.of(dto.getPage() - 1, dto.getSize(), Sort.Direction.DESC, "UM_CREATED_AT");

        return emailDataProvider.findAllByUser(userId, dto.getKeyword(), pageable);
    }

    private EmailsSentPaginationOutputDTO mountOutput(Page<Email> userEmails) {
        return EmailsSentPaginationOutputDTO.builder()
            .page(userEmails.getNumber() + 1)
            .size(userEmails.getSize())
            .totalItems(userEmails.getTotalElements())
            .emails(userEmails.getContent().stream().map(email ->
                    EmailSentOutputDTO.builder()
                        .id(email.getId())
                        .subject(email.getSubject())
                        .message(email.getMessage())
                        .createdAt(email.getCreatedAt())
                        .ccs(email.getCCopies().stream().map(copy ->
                            CarbonCopyOutputDTO.builder()
                                .id(copy.getId())
                                .user(GetUserProfileOutputDTO.builder()
                                    .id(copy.getUser().getId())
                                    .role(copy.getUser().getRole())
                                    .name(copy.getUser().getName())
                                    .profilePicture(copy.getUser().getProfilePicture())
                                    .email(copy.getUser().getEmail())
                                    .createdAt(copy.getUser().getCreatedAt())
                                    .build()
                                ).build()
                        ).toList())
                        .build())
                .toList())
            .build();
    }
}
