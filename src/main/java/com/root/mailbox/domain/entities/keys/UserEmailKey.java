package com.root.mailbox.domain.entities.keys;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class UserEmailKey implements Serializable {
    private Long userId;
    private UUID emailId;
}
