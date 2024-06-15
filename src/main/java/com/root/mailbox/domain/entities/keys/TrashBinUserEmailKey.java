package com.root.mailbox.domain.entities.keys;

import com.root.mailbox.domain.entities.User;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class TrashBinUserEmailKey implements Serializable {
    @JoinColumn(name = "UTE_TRASH_BIN_ID", nullable = false)
    private Long trashBinId;

    @JoinColumn(name = "UTE_EMAIL_ID", nullable = true)
    private UUID emailId;

    @JoinColumn(name = "UTE_USER_ID", nullable = true)
    private User user;
}
