package com.root.mailbox.presentation.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String recoverEmail;
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String profilePicture;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean provisoryPassword;
    private Role role;
    private Date createdAt;
}
