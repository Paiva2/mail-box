package com.root.mailbox.domain.entities.keys;

import jakarta.persistence.Embeddable;
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
    private UUID trashBinId;
    private UUID emailId;
    private Long userId;
}
