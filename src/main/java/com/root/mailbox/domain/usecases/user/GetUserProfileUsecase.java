package com.root.mailbox.domain.usecases.user;

import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.presentation.dto.user.GetUserProfileOutputDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class GetUserProfileUsecase {
    private final UserDataProvider userDataProvider;

    public GetUserProfileOutputDTO exec(Long userId) {
        User user = checkIfUserExists(userId);

        if (user.getDisabled()) {
            throw new UserDisabledException(userId);
        }

        return mountOutput(user);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private GetUserProfileOutputDTO mountOutput(User user) {
        return GetUserProfileOutputDTO.builder()
            .id(user.getId())
            .email(user.getEmail())
            .name(user.getName())
            .profilePicture(user.getProfilePicture())
            .role(user.getRole())
            .recoverEmail(user.getRecoverEmail())
            .provisoryPassword(user.getProvisoryPassword())
            .createdAt(user.getCreatedAt())
            .build();
    }

}
