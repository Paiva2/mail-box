package com.root.mailbox.domain.usecases.user;

import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.exceptions.UserAlreadyExistsException;
import com.root.mailbox.domain.exceptions.UserDisabledException;
import com.root.mailbox.domain.exceptions.UserNotFoundException;
import com.root.mailbox.domain.exceptions.WeakPasswordException;
import com.root.mailbox.domain.utils.CopyClassProperties;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.presentation.dto.user.GetUserProfileOutputDTO;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UpdateUserProfileUsecase {
    private final UserDataProvider userDataProvider;
    private final PasswordEncoder passwordEncoder;
    private CopyClassProperties<User> copyClassProperties;

    public GetUserProfileOutputDTO exec(User userUpdated) {
        User user = checkIfUserExists(userUpdated.getId());

        if (Objects.nonNull(user.getDisabled())) {
            throw new UserDisabledException(user.getId());
        }

        if (Objects.nonNull(userUpdated.getEmail()) && !user.getEmail().equals(userUpdated.getEmail())) {
            checkIfNewEmailExists(userUpdated.getEmail());
        }

        if (Objects.nonNull(userUpdated.getPassword())) {
            encodeNewPassword(userUpdated);
        }

        copyClassProperties.copyNonNull(userUpdated, user);

        User userUpdate = persistUpdatedUser(user);

        return mountOutput(userUpdate);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private void checkIfNewEmailExists(String userEmail) {
        Optional<User> user = userDataProvider.findUserByEmail(userEmail);

        if (user.isPresent()) {
            throw new UserAlreadyExistsException("e-mail");
        }
    }

    private void encodeNewPassword(User user) {
        if (user.getPassword().length() < 6) {
            throw new WeakPasswordException();
        }

        String passwordEncoded = passwordEncoder.encode(user.getPassword());
        user.setPassword(passwordEncoded);
    }

    private User persistUpdatedUser(User userUpdated) {
        return userDataProvider.create(userUpdated);
    }

    private GetUserProfileOutputDTO mountOutput(User user) {
        return GetUserProfileOutputDTO.builder()
            .id(user.getId())
            .email(user.getEmail())
            .name(user.getName())
            .profilePicture(user.getProfilePicture())
            .role(user.getRole())
            .createdAt(user.getCreatedAt())
            .build();
    }
}
