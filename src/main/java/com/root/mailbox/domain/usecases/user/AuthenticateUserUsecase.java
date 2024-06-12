package com.root.mailbox.domain.usecases.user;

import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.domain.exceptions.user.WrongCredentialsException;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.presentation.dto.user.AuthenticateUserOutputDTO;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticateUserUsecase {
    private final UserDataProvider userDataProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthenticateUserOutputDTO exec(User user) {
        User getUser = checkIfUserExists(user.getEmail());

        doesPasswordsMatches(getUser.getPassword(), user.getPassword());

        return mountOutput(getUser);
    }

    private User checkIfUserExists(String email) {
        return userDataProvider.findUserByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }

    private void doesPasswordsMatches(String encoded, String raw) {
        boolean passwordsMatches = passwordEncoder.matches(raw, encoded);

        if (!passwordsMatches) {
            throw new WrongCredentialsException();
        }
    }

    private AuthenticateUserOutputDTO mountOutput(User user) {
        return AuthenticateUserOutputDTO.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .role(user.getRole())
            .profilePicture(user.getProfilePicture())
            .createdAt(user.getCreatedAt())
            .build();
    }
}
