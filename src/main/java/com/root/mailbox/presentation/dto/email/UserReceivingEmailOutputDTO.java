package com.root.mailbox.presentation.dto.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserReceivingEmailOutputDTO {
    private Long id;
    private String email;
    private String name;
    private String profilePicture;
    private Date createdAt;
}
