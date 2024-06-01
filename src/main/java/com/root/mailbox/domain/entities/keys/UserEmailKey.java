package com.root.mailbox.domain.entities.keys;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class UserEmailKey implements Serializable {

    @JoinColumn(name = "UM_USER_ID")
    private Long userId;

    @JoinColumn(name = "UM_EMAIL_ID")
    private UUID emailId;
}
