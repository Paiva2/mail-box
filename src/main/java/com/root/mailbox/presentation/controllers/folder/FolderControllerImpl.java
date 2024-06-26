package com.root.mailbox.presentation.controllers.folder;

import com.root.mailbox.domain.usecases.folder.CreateFolderUsecase;
import com.root.mailbox.presentation.dto.folder.CreateFolderInputDTO;
import com.root.mailbox.presentation.dto.folder.FolderOutputDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class FolderControllerImpl implements FolderController {
    private final CreateFolderUsecase createFolderUsecase;

    @Override
    public ResponseEntity<FolderOutputDTO> create(
        Authentication authentication,
        @RequestBody @Valid CreateFolderInputDTO dto
    ) {
        Long userId = Long.valueOf(authentication.getName());
        FolderOutputDTO output = createFolderUsecase.exec(userId, dto.toFolder());

        return new ResponseEntity<>(output, HttpStatus.CREATED);
    }
}
