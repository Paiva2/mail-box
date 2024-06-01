package com.root.mailbox.infra.providers;

import com.root.mailbox.domain.entities.Email;
import com.root.mailbox.domain.entities.UserEmail;
import com.root.mailbox.infra.repositories.EmailRepository;
import com.root.mailbox.infra.repositories.UserEmailRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EmailDataProvider {
    private EmailRepository emailRepository;
    private UserEmailRepository userEmailRepository;

    public Email create(Email email) {
        return emailRepository.save(email);
    }

    public UserEmail createUserEmail(UserEmail userEmail) {
        return userEmailRepository.save(userEmail);
    }
}
