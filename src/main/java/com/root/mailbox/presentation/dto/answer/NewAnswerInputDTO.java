package com.root.mailbox.presentation.dto.answer;

import com.root.mailbox.domain.entities.Answer;
import com.root.mailbox.domain.entities.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NewAnswerInputDTO {
    @NotBlank
    private String message;

    public Answer toAnswer() {
        return Answer.builder()
            .message(this.message)
            .build();
    }
}
