package com.root.mailbox.infra.providers;

import com.root.mailbox.domain.entities.Answer;
import com.root.mailbox.infra.repositories.AnswerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Component
public class AnswerDataProvider {
    private final AnswerRepository answerRepository;

    public Answer save(Answer answer) {
        return answerRepository.save(answer);
    }

    public Optional<Answer> findByIdAndUserId(UUID answerId, Long userId) {
        return answerRepository.findByIdAndUserId(answerId, userId);
    }
}
