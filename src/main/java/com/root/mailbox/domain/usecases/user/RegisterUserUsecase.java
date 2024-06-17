package com.root.mailbox.domain.usecases.user;

import com.root.mailbox.domain.entities.TrashBin;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.enums.Role;
import com.root.mailbox.domain.exceptions.user.UserAlreadyExistsException;
import com.root.mailbox.domain.exceptions.user.WeakPasswordException;
import com.root.mailbox.infra.providers.TrashBinDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.presentation.dto.user.RegisterUserOutputDTO;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class RegisterUserUsecase {
    private final UserDataProvider userDataProvider;
    private final PasswordEncoder passwordEncoder;
    private final TrashBinDataProvider trashBinDataProvider;

    public RegisterUserOutputDTO exec(User newUser) {
        checkIfEmailAlreadyExists(newUser);

        if (newUser.getPassword().length() < 6) {
            throw new WeakPasswordException();
        }

        hashNewPassword(newUser);

        User userCreated = createNewUser(newUser);

        createUserTrashbin(userCreated);

        return mountOutput(userCreated);
    }

    private void checkIfEmailAlreadyExists(User newUser) {
        Optional<User> findUser = userDataProvider.findUserByEmail(newUser.getEmail());

        if (findUser.isPresent()) {
            throw new UserAlreadyExistsException("E-mail");
        }
    }

    private void hashNewPassword(User newUser) {
        String passwordHashed = passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(passwordHashed);
    }

    private User createNewUser(User newUser) {
        newUser.setRole(Role.USER);
        newUser.setDisabled(false);

        return userDataProvider.create(newUser);
    }

    private void createUserTrashbin(User user) {
        TrashBin trashBin = TrashBin.builder()
            .user(user)
            .build();

        trashBinDataProvider.create(trashBin);
    }

    private RegisterUserOutputDTO mountOutput(User newUser) {
        return RegisterUserOutputDTO.builder()
            .id(newUser.getId())
            .name(newUser.getName())
            .email(newUser.getEmail())
            .build();
    }
}
