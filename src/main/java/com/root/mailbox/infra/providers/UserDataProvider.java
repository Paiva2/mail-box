package com.root.mailbox.infra.providers;

import com.root.mailbox.domain.entities.User;
import com.root.mailbox.infra.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class UserDataProvider {
    private final UserRepository repository;

    public Optional<User> findUserByEmail(String email) {
        return repository.findByEmail(email);
    }

    public User create(User newUser) {
        return repository.save(newUser);
    }

    public Optional<User> findUserById(Long userId) {
        return repository.findById(userId);
    }
}
