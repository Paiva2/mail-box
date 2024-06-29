package com.root.mailbox.presentation.controllers.email;

import com.root.mailbox.domain.usecases.email.*;
import com.root.mailbox.presentation.dto.email.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class EmailControllerImpl implements EmailController {
    private final NewEmailUsecase newEmailUsecase;
    private final GetInboxUsecase getInboxUsecase;
    private final FilterEmailToMeUsecase filterEmailToMeUsecase;
    private final ListEmailsSentUsecase listEmailsSentUsecase;
    private final EmailSpamUsecase emailSpamUsecase;
    private final FilterEmailSentUsecase filterEmailSentUsecase;
    private final UnOpenEmailUsecase unOpenEmailUsecase;
    private final NewEmailAsDraftUsecase newEmailAsDraftUsecase;
    private final ListDraftEmailsUsecase listDraftEmailsUsecase;
    private final DeleteDraftEmailUsecase deleteDraftEmailUsecase;
    private final SendDraftEmailUsecase sendDraftEmailUsecase;
    private final HandleUserEmailFolderUsecase handleUserEmailFolderUsecase;

    @Override
    public ResponseEntity<Void> create(
        Authentication authentication,
        @RequestBody @Valid NewEmailInputDTO dto
    ) {
        Long userId = Long.valueOf(authentication.getName());
        newEmailUsecase.exec(dto.toEmail(), userId);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> createDraft(
        Authentication authentication,
        @RequestBody @Valid NewDraftEmailInputDTO dto
    ) {
        Long userId = Long.valueOf(authentication.getName());
        newEmailAsDraftUsecase.exec(userId, dto.toEmail());

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<EmailInboxOutputDTO> getInbox(
        Authentication authentication,
        @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
        @RequestParam(name = "size", required = false, defaultValue = "5") Integer size,
        @RequestParam(name = "keyword", required = false) String keyword,
        @RequestParam(name = "spam", required = false) Boolean filteringSpam,
        @RequestParam(name = "opened", required = false) Boolean opened
    ) {
        Long userId = Long.valueOf(authentication.getName());
        EmailInboxOutputDTO output = getInboxUsecase.exec(userId, InboxPaginationDTO.builder()
            .page(page)
            .size(size)
            .keyword(keyword)
            .filteringSpam(filteringSpam)
            .opened(opened)
            .build()
        );

        return new ResponseEntity<>(output, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EmailOutputDTO> getEmailToMe(
        Authentication authentication,
        @PathVariable("emailId") UUID emailId
    ) {
        Long userId = Long.valueOf(authentication.getName());
        EmailOutputDTO output = filterEmailToMeUsecase.exec(userId, emailId);

        return new ResponseEntity<>(output, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EmailsSentPaginationOutputDTO> getSent(
        Authentication authentication,
        @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
        @RequestParam(name = "size", required = false, defaultValue = "5") Integer size,
        @RequestParam(name = "keyword", required = false) String keyword
    ) {
        Long userId = Long.valueOf(authentication.getName());
        EmailsSentPaginationOutputDTO output = listEmailsSentUsecase.exec(userId, EmailsSentPaginationInputDTO.builder()
            .page(page)
            .size(size)
            .keyword(keyword)
            .build()
        );

        return new ResponseEntity<>(output, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ListDraftEmailsOutputDTO> getDrafts(
        Authentication authentication,
        @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
        @RequestParam(name = "size", required = false, defaultValue = "5") Integer size,
        @RequestParam(name = "keyword", required = false) String keyword
    ) {
        Long userId = Long.valueOf(authentication.getName());
        ListDraftEmailsOutputDTO output = listDraftEmailsUsecase.exec(userId, ListDraftEmailsPaginationInputDTO.builder()
            .page(page)
            .size(size)
            .keyword(keyword)
            .build()
        );

        return new ResponseEntity<>(output, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EmailSentOutputDTO> filterSent(
        Authentication authentication,
        @PathVariable("emailId") UUID emailId
    ) {
        Long userId = Long.valueOf(authentication.getName());
        EmailSentOutputDTO output = filterEmailSentUsecase.exec(userId, emailId);

        return new ResponseEntity<>(output, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EmailOutputDTO> handleSpam(
        Authentication authentication,
        @PathVariable("emailId") UUID emailId,
        @RequestParam(name = "setSpam", required = false, defaultValue = "true") Boolean setSpam
    ) {
        Long userId = Long.valueOf(authentication.getName());
        EmailOutputDTO output = emailSpamUsecase.exec(userId, emailId, setSpam);

        return new ResponseEntity<>(output, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EmailOutputDTO> unopenEmail(
        Authentication authentication,
        @PathVariable("emailId") UUID emailId
    ) {
        Long userId = Long.valueOf(authentication.getName());
        EmailOutputDTO output = unOpenEmailUsecase.exec(userId, emailId);

        return new ResponseEntity<>(output, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteDraft(
        Authentication authentication,
        @PathVariable("emailId") UUID emailId
    ) {
        Long userId = Long.valueOf(authentication.getName());
        deleteDraftEmailUsecase.exec(userId, emailId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<Void> sendDraft(
        Authentication authentication,
        @PathVariable("emailId") UUID emailId,
        @RequestBody @Valid SendDraftEmailInputDTO dto
    ) {
        Long userId = Long.valueOf(authentication.getName());
        sendDraftEmailUsecase.exec(userId, dto.toEmail(emailId));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<Void> updateFolder(
        Authentication authentication,
        @PathVariable("emailId") UUID emailId,
        @RequestBody @Valid UpdateFolderInputDTO dto) {
        Long userId = Long.valueOf(authentication.getName());
        handleUserEmailFolderUsecase.exec(userId, emailId, dto.getFolderId());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
