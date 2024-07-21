package com.root.mailbox.domain.usecases.user;

import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.domain.utils.EmailSenderAdapter;
import com.root.mailbox.infra.providers.UserDataProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ForgotPasswordUsecase {
    private final static String EMAIL_TITLE = "Mail-Box - Forgot Password";
    private final static String EMAIL_TEMPLATE = "forgot-password";

    private final UserDataProvider userDataProvider;
    private final PasswordEncoder passwordEncoder;
    private final EmailSenderAdapter emailSenderAdapter;

    @Transactional
    public void exec(String email) {
        User user = checkIfUserExists(email);

        if (user.getDisabled()) {
            throw new UserDisabledException(user.getId());
        }

        String newPassword = handleRandomNewPassword(user);

        persistNewPassword(user);

        List<String> mailParams = List.of(user.getName(), newPassword);

        emailSenderAdapter.forgotPasswordMail(user.getRecoverEmail(), EMAIL_TITLE, mailParams, EMAIL_TEMPLATE);
    }

    private User checkIfUserExists(String email) {
        return userDataProvider.findUserByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }

    private String handleRandomNewPassword(User user) {
        String randomPass = UUID.randomUUID().toString();
        String hashedPass = passwordEncoder.encode(randomPass);

        user.setPassword(hashedPass);
        user.setProvisoryPassword(true);

        return randomPass;
    }

    private void persistNewPassword(User user) {
        userDataProvider.create(user);
    }
}
