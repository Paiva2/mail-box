package com.root.mailbox.presentation.controllers.email;

import com.root.mailbox.domain.usecases.email.GetInboxUsecase;
import com.root.mailbox.domain.usecases.email.NewEmailUsecase;
import com.root.mailbox.presentation.dto.email.InboxPaginationDTO;
import com.root.mailbox.presentation.dto.email.ListInboxOutputDTO;
import com.root.mailbox.presentation.dto.email.NewEmailInputDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class EmailControllerImpl implements EmailController {
    private final NewEmailUsecase newEmailUsecase;
    private final GetInboxUsecase getInboxUsecase;

    @Override
    public ResponseEntity<Void> create(
        Authentication authentication,
        @RequestBody @Valid NewEmailInputDTO dto
    ) {
        Long userId = Long.valueOf(authentication.getName());
        newEmailUsecase.exec(dto.toEmail(), userId);

        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ListInboxOutputDTO> getInbox(
        Authentication authentication,
        @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
        @RequestParam(name = "size", required = false, defaultValue = "5") Integer size,
        @RequestParam(name = "keyword", required = false) String keyword
    ) {
        Long userId = Long.valueOf(authentication.getName());
        ListInboxOutputDTO output = getInboxUsecase.exec(userId, InboxPaginationDTO.builder()
            .page(page)
            .size(size)
            .keyword(keyword)
            .build()
        );

        return new ResponseEntity<>(output, HttpStatus.OK);
    }
}
