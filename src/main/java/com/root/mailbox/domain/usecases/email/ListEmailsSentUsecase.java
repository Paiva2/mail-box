package com.root.mailbox.domain.usecases.email;

import com.root.mailbox.domain.entities.Email;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.entities.UserEmail;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.infra.providers.UserEmailDataProvider;
import com.root.mailbox.presentation.dto.attachment.AttachmentOutputDTO;
import com.root.mailbox.presentation.dto.email.*;
import com.root.mailbox.presentation.dto.user.GetUserProfileOutputDTO;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Objects;

@AllArgsConstructor
@Service
public class ListEmailsSentUsecase {
    private UserDataProvider userDataProvider;
    private UserEmailDataProvider userEmailDataProvider;

    public EmailsSentPaginationOutputDTO exec(Long userId, EmailsSentPaginationInputDTO dto) {
        User user = checkIfUserExists(userId);
        handlePagination(dto);

        Page<UserEmail> sentEmails = getSentEmails(user.getId(), dto);

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

    private Page<UserEmail> getSentEmails(Long userId, EmailsSentPaginationInputDTO dto) {
        Pageable pageable = PageRequest.of(dto.getPage() - 1, dto.getSize(), Sort.Direction.DESC, "UM_CREATED_AT");

        return userEmailDataProvider.findAllSentByUser(userId, dto.getKeyword(), pageable);
    }

    private EmailsSentPaginationOutputDTO mountOutput(Page<UserEmail> emailsPagination) {
        return EmailsSentPaginationOutputDTO.builder()
            .page(emailsPagination.getNumber() + 1)
            .size(emailsPagination.getSize())
            .totalItems(emailsPagination.getTotalElements())
            .emails(emailsPagination.getContent().stream().map(userEmail ->
                    EmailSentOutputDTO.builder()
                        .id(userEmail.getEmail().getId())
                        .subject(userEmail.getEmail().getSubject())
                        .message(userEmail.getEmail().getMessage())
                        .createdAt(userEmail.getEmail().getCreatedAt())
                        .hasOrder(userEmail.getEmail().getOpeningOrders())
                        .attachments(userEmail.getEmail().getEmailAttachments().stream().map(
                                emailAttachment -> AttachmentOutputDTO.builder()
                                    .id(emailAttachment.getAttachment().getId())
                                    .fileName(emailAttachment.getAttachment().getFileName())
                                    .url(emailAttachment.getAttachment().getUrl())
                                    .createdAt(emailAttachment.getAttachment().getCreatedAt())
                                    .build()
                            ).toList()
                        )
                        .openingOrders(Objects.nonNull(userEmail.getEmail().getEmailOpeningOrders()) ?
                            userEmail.getEmail().getEmailOpeningOrders().stream().map(order ->
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
                        .usersReceivingEmailOutput(userEmail.getEmail().getUsersEmails().stream().filter(copy ->
                                copy.getEmailType().equals(UserEmail.EmailType.RECEIVED)
                            ).map(copy -> UserReceivingEmailOutputDTO.builder()
                                .id(copy.getUser().getId())
                                .name(copy.getUser().getName())
                                .profilePicture(copy.getUser().getProfilePicture())
                                .email(copy.getUser().getEmail())
                                .createdAt(copy.getUser().getCreatedAt())
                                .build())
                            .toList())
                        .ccs(userEmail.getEmail().getUsersEmails().stream().filter(copy ->
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
