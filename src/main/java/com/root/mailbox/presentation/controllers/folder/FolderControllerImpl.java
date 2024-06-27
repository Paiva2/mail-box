package com.root.mailbox.presentation.controllers.folder;

import com.root.mailbox.domain.usecases.folder.CreateFolderUsecase;
import com.root.mailbox.domain.usecases.folder.InsertUserEmailOnFolderUsecase;
import com.root.mailbox.domain.usecases.folder.ListAllRootFoldersUsecase;
import com.root.mailbox.presentation.dto.folder.CreateFolderInputDTO;
import com.root.mailbox.presentation.dto.folder.FolderOutputDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class FolderControllerImpl implements FolderController {
    private final CreateFolderUsecase createFolderUsecase;
    private final InsertUserEmailOnFolderUsecase insertUserEmailOnFolderUsecase;
    private final ListAllRootFoldersUsecase listAllRootFoldersUsecase;

    @Override
    public ResponseEntity<FolderOutputDTO> create(
        Authentication authentication,
        @RequestBody @Valid CreateFolderInputDTO dto
    ) {
        Long userId = Long.valueOf(authentication.getName());
        FolderOutputDTO output = createFolderUsecase.exec(userId, dto.toFolder());

        return new ResponseEntity<>(output, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<FolderOutputDTO>> listRoot(
        Authentication authentication
    ) {
        Long userId = Long.valueOf(authentication.getName());
        List<FolderOutputDTO> output = listAllRootFoldersUsecase.exec(userId);

        return new ResponseEntity<>(output, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> insertEmail(
        Authentication authentication,
        @PathVariable("emailId") UUID emailId,
        @PathVariable("folderId") Long folderId
    ) {
        Long userId = Long.valueOf(authentication.getName());
        insertUserEmailOnFolderUsecase.exec(userId, emailId, folderId);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
