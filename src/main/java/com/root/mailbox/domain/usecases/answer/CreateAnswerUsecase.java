package com.root.mailbox.domain.usecases.answer;

import com.root.mailbox.domain.entities.*;
import com.root.mailbox.domain.exceptions.generic.ForbiddenException;
import com.root.mailbox.domain.exceptions.openingOrder.OpeningOrderNotFoundException;
import com.root.mailbox.domain.exceptions.user.UserDisabledException;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.domain.exceptions.userEmail.UserEmailNotFoundException;
import com.root.mailbox.infra.providers.AnswerDataProvider;
import com.root.mailbox.infra.providers.EmailOpeningOrderDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.infra.providers.UserEmailDataProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CreateAnswerUsecase {
    private final UserDataProvider userDataProvider;
    private final UserEmailDataProvider userEmailDataProvider;
    private final EmailOpeningOrderDataProvider emailOpeningOrderDataProvider;
    private final AnswerDataProvider answerDataProvider;

    public void exec(Long userId, UUID emailId, Answer newAnswer) {
        User user = checkIfUserExists(userId);

        if (user.getDisabled()) {
            throw new UserDisabledException(userId);
        }

        UserEmail userEmail = checkIfUserEmailExists(user.getId(), emailId);

        if (userEmail.getDisabled()) {
            throw new UserEmailNotFoundException(emailId.toString(), user.getId().toString());
        }

        Email email = userEmail.getEmail();

        if (email.getOpeningOrders()) {
            checkIfUserCanAnswerOrderBased(user.getId(), email.getId());
        }

        newAnswer.setEmail(email);
        newAnswer.setUser(user);

        persistNewAnswer(newAnswer);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private UserEmail checkIfUserEmailExists(Long userId, UUID emailId) {
        return userEmailDataProvider.findUserEmail(userId, emailId).orElseThrow(() -> new UserEmailNotFoundException(emailId.toString(), userId.toString()));
    }

    private void checkIfUserCanAnswerOrderBased(Long userId, UUID emailId) {
        List<EmailOpeningOrder> openingOrder = emailOpeningOrderDataProvider.findAllByEmail(emailId);
        Optional<EmailOpeningOrder> userOrder = openingOrder.stream().filter(order -> order.getUser().getId().equals(userId)).findAny();

        if (openingOrder.isEmpty() || userOrder.isEmpty()) {
            throw new OpeningOrderNotFoundException();
        }

        Integer userOrderNumber = userOrder.get().getOrder();

        Optional<EmailOpeningOrder> hasOpeningOrderBeforeAndNotOpened = openingOrder.stream().filter(order -> order.getOrder() < userOrderNumber && order.getStatus().equals(EmailOpeningOrder.OpeningStatus.NOT_OPENED)).findAny();

        if (hasOpeningOrderBeforeAndNotOpened.isPresent()) {
            throw new ForbiddenException();
        }
    }

    private void persistNewAnswer(Answer answer) {
        answer.setDisabled(false);
        answerDataProvider.save(answer);
    }
}
