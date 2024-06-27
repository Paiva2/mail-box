package com.root.mailbox.domain.entities;

import com.root.mailbox.domain.entities.keys.UserEmailKey;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Objects;

/**
 * UserEmail is treated as a link to the original Email entity that was
 * sent to all Users, each Email has a User as creator, each person to receive the Email
 * has a UserEmail register on database
 **/
@Getter
@Setter
@Entity
@Table(name = "tb_users_emails")
public class UserEmail {
    @EmbeddedId
    private UserEmailKey userEmailKey = new UserEmailKey();

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("userId")
    @JoinColumn(name = "UM_USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("emailId")
    @JoinColumn(name = "UM_EMAIL_ID")
    private Email email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UM_FOLDER_ID", nullable = true)
    private Folder folder;

    @Enumerated(EnumType.STRING)
    @Column(name = "UM_EMAIL_TYPE", nullable = false)
    private EmailType emailType;

    @Column(name = "UM_OPENED", nullable = false)
    private Boolean opened;

    @Column(name = "UM_IS_SPAM", nullable = false)
    private Boolean isSpam;

    @Column(name = "UM_DISABLED", nullable = false)
    private Boolean disabled;

    @Column(name = "UM_DELETED_AT", nullable = true)
    private Date deletedAt;

    @CreationTimestamp
    @Column(name = "UM_CREATED_AT")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "UM_UPDATED_AT")
    private Date updatedAt;

    public UserEmail() {
    }

    public UserEmail(User user, Email email, Boolean isSpam, Boolean disabled, EmailType emailType) {
        this.user = user;
        this.email = email;
        this.isSpam = isSpam;
        this.disabled = disabled;
        this.emailType = emailType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEmail userEmail = (UserEmail) o;
        return Objects.equals(userEmailKey, userEmail.userEmailKey) && Objects.equals(user, userEmail.user) && Objects.equals(email, userEmail.email) && Objects.equals(createdAt, userEmail.createdAt) && Objects.equals(updatedAt, userEmail.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userEmailKey, user, email, createdAt, updatedAt);
    }

    public enum EmailType {
        MINE,
        RECEIVED,
        IN_COPY;

        public String getType() {
            return this.name();
        }
    }
}
