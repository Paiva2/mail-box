package com.root.mailbox.domain.usecases.email;

import com.root.mailbox.domain.entities.Email;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.entities.UserEmail;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.infra.providers.EmailDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.presentation.dto.attachment.AttachmentOutputDTO;
import com.root.mailbox.presentation.dto.email.*;
import com.root.mailbox.presentation.dto.user.GetUserProfileOutputDTO;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        Pageable pageable = PageRequest.of(dto.getPage() - 1, dto.getSize(), Sort.Direction.DESC, "EM_CREATED_AT");

        return emailDataProvider.findAllByUser(userId, dto.getKeyword(), pageable);
    }

    private EmailsSentPaginationOutputDTO mountOutput(Page<Email> emailsPagination) {
        return EmailsSentPaginationOutputDTO.builder()
            .page(emailsPagination.getNumber() + 1)
            .size(emailsPagination.getSize())
            .totalItems(emailsPagination.getTotalElements())
            .emails(emailsPagination.getContent().stream().map(email ->
                    EmailSentOutputDTO.builder()
                        .id(email.getId())
                        .subject(email.getSubject())
                        .message(email.getMessage())
                        .createdAt(email.getCreatedAt())
                        .hasOpeningOrder(email.getOpeningOrders())
                        .attachments(email.getAttachments().stream().map(
                                attachment -> AttachmentOutputDTO.builder()
                                    .id(attachment.getId())
                                    .fileName(attachment.getFileName())
                                    .url(attachment.getUrl())
                                    .createdAt(attachment.getCreatedAt())
                                    .build()
                            ).toList()
                        )
                        .openingOrders(Objects.nonNull(email.getEmailOpeningOrders()) ?
                            email.getEmailOpeningOrders().stream().map(order ->
                                    EmailOpeningOrderOutputDTO.builder()
                                        .id(order.getId())
                                        .status(order.getStatus())
                                        .order(order.getOrder())
                                        .user(GetUserProfileOutputDTO.builder()
                                            .id(order.getUser().getId())
                                            .name(order.getUser().getName())
                                            .role(order.getUser().getRole())
                                            .profilePicture(order.getUser().getProfilePicture())
                                            .email(order.getUser().getEmail())
                                            .createdAt(order.getUser().getCreatedAt())
                                            .build()
                                        ).build())
                                .toList() : null)
                        .usersReceivingEmailOutput(email.getUsersEmails().stream().filter(copy ->
                                copy.getEmailType().equals(UserEmail.EmailType.RECEIVED)
                            ).map(copy -> UserReceivingEmailOutputDTO.builder()
                                .id(copy.getUser().getId())
                                .name(copy.getUser().getName())
                                .profilePicture(copy.getUser().getProfilePicture())
                                .email(copy.getUser().getEmail())
                                .createdAt(copy.getUser().getCreatedAt())
                                .build())
                            .toList())
                        .ccs(email.getUsersEmails().stream().filter(copy ->
                            copy.getEmailType().equals(UserEmail.EmailType.IN_COPY)
                        ).map(copy ->
                            CarbonCopyOutputDTO.builder()
                                .id(copy.getUser().getId())
                                .name(copy.getUser().getName())
                                .profilePicture(copy.getUser().getProfilePicture())
                                .email(copy.getUser().getEmail())
                                .createdAt(copy.getUser().getCreatedAt())
                                .build()
                        ).toList())
                        .build())
                .toList())
            .build();
    }
}
