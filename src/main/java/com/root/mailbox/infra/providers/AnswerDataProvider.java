package com.root.mailbox.infra.providers;

import com.root.mailbox.domain.entities.Answer;
import com.root.mailbox.infra.repositories.AnswerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class AnswerDataProvider {
    private final AnswerRepository answerRepository;

    public Answer save(Answer answer) {
        return answerRepository.save(answer);
    }
}
