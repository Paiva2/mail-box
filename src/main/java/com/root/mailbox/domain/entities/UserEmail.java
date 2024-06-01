package com.root.mailbox.domain.entities;

import com.root.mailbox.domain.entities.keys.UserEmailKey;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
@Table(name = "tb_users_emails")
public class UserEmail {
    @EmbeddedId
    private UserEmailKey userEmailKey = new UserEmailKey();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "UM_USER_ID")
    @ToString.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("emailId")
    @JoinColumn(name = "UM_EMAIL_ID")
    @ToString.Exclude
    private Email email;

    @CreationTimestamp
    @Column(name = "UM_CREATED_AT")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "UM_UPDATED_AT")
    private Date updatedAt;

    public UserEmail() {
    }

    public UserEmail(User user, Email email) {
        this.email = email;
        this.user = user;
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
}
