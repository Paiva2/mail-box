package com.root.mailbox.infra.providers;

import com.root.mailbox.domain.entities.User;
import com.root.mailbox.infra.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class UserDataProvider {
    private final UserRepository userRepository;

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmailAndEnabled(email);
    }

    public User create(User newUser) {
        return userRepository.save(newUser);
    }

    public Optional<User> findUserById(Long userId) {
        return userRepository.findByIdEnabled(userId);
    }
}
