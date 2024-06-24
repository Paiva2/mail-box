package com.root.mailbox.presentation.controllers.trashBin;

import com.root.mailbox.domain.usecases.trashBin.DeleteUserEmailFromTrashUsecase;
import com.root.mailbox.domain.usecases.trashBin.ListTrashEmailsUsecase;
import com.root.mailbox.domain.usecases.trashBin.RecoverEmailFromTrashUsecase;
import com.root.mailbox.domain.usecases.trashBin.SendUserEmailToTrashUsecase;
import com.root.mailbox.presentation.dto.email.ListTrashEmailsOutputDTO;
import com.root.mailbox.presentation.dto.email.ListTrashEmailsPaginationDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class TrashBinControllerImpl implements TrashBinController {
    private final SendUserEmailToTrashUsecase sendUserEmailToTrashUsecase;
    private final ListTrashEmailsUsecase listTrashEmailsUsecase;
    private final DeleteUserEmailFromTrashUsecase deleteUserEmailFromTrashUsecase;
    private final RecoverEmailFromTrashUsecase recoverEmailFromTrashUsecase;

    @Override
    public ResponseEntity<Void> moveToTrash(
        Authentication authentication,
        @PathVariable("emailId") UUID emailId
    ) {
        Long userId = Long.valueOf(authentication.getName());
        sendUserEmailToTrashUsecase.exec(userId, emailId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<ListTrashEmailsOutputDTO> getTrash(
        Authentication authentication,
        @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
        @RequestParam(name = "size", required = false, defaultValue = "5") Integer size,
        @RequestParam(name = "keyword", required = false) String keyword,
        @RequestParam(name = "spam", required = false) Boolean spam,
        @RequestParam(name = "opened", required = false) Boolean opened
    ) {
        Long userId = Long.valueOf(authentication.getName());
        ListTrashEmailsOutputDTO output = listTrashEmailsUsecase.exec(userId, ListTrashEmailsPaginationDTO.builder()
            .page(page)
            .size(size)
            .keyword(keyword)
            .spam(spam)
            .opened(opened)
            .build()
        );

        return new ResponseEntity<>(output, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteFromTrash(
        Authentication authentication,
        @PathVariable("emailId") UUID emailId
    ) {
        Long userId = Long.valueOf(authentication.getName());
        deleteUserEmailFromTrashUsecase.exec(userId, emailId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<Void> recoverFromTrash(
        Authentication authentication,
        @PathVariable("emailId") UUID emailId
    ) {
        Long userId = Long.valueOf(authentication.getName());
        recoverEmailFromTrashUsecase.exec(userId, emailId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
