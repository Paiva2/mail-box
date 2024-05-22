package com.root.mailbox.presentation.dto.user;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RegisterUserOutputDTO {
    private Long id;
    private String email;
    private String name;
}
