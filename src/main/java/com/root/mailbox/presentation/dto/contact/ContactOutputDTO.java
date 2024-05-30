package com.root.mailbox.presentation.dto.contact;

import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ContactOutputDTO {
    private String name;
    private String email;
    private Date createdAt;
}
