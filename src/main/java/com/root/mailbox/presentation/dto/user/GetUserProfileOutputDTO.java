package com.root.mailbox.presentation.dto.user;

import com.root.mailbox.domain.enums.Role;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GetUserProfileOutputDTO {
    private Long id;
    private String email;
    private String name;
    private String profilePicture;
    private Role role;
    private Date createdAt;
}
