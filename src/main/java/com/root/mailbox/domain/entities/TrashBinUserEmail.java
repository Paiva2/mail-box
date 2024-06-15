package com.root.mailbox.domain.entities;

import com.root.mailbox.domain.entities.keys.TrashBinUserEmailKey;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "tb_users_trash_bin_emails")
public class TrashBinUserEmail {
    @EmbeddedId
    private TrashBinUserEmailKey userEmailKey = new TrashBinUserEmailKey();

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("trashBinId")
    @JoinColumn(name = "UTE_TRASH_BIN_ID", nullable = false)
    private TrashBin trashBin;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("emailId")
    @JoinColumn(name = "UTE_EMAIL_ID", nullable = true)
    private Email email;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("userId")
    @JoinColumn(name = "UTE_USER_ID", nullable = true)
    private User user;

    @CreationTimestamp
    @Column(name = "UTE_CREATED_AT")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "UTE_UPDATED_AT")
    private Date updatedAt;
}
