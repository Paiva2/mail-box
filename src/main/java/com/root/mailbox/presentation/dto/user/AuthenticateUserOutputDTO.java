package com.root.mailbox.presentation.dto.user;

import com.root.mailbox.domain.enums.Role;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AuthenticateUserOutputDTO {
    private Long id;
    private String name;
    private String email;
    private String profilePicture;
    private Role role;
    private Date createdAt;
}
