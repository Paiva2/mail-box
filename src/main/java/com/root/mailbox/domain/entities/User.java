package com.root.mailbox.domain.entities;

import com.root.mailbox.domain.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tb_users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "USR_ID")
    private Long id;

    @Column(name = "USR_NAME")
    private String name;

    @Column(name = "USR_EMAIL", unique = true)
    private String email;

    @Column(name = "USR_PASSWORD")
    private String password;

    @Column(name = "USR_PROFILE_PICTURE", nullable = true)
    private String profilePicture;

    @Column(name = "USR_ROLE")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "USR_DISABLED", nullable = true)
    private Boolean disabled = false;

    @Column(name = "USR_DISABLED_AT", nullable = true)
    private Date disabledAt;

    @CreationTimestamp
    @Column(name = "USR_CREATED_AT")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "USR_UPDATED_AT")
    private Date updatedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Attachment> attachments;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Email> emails;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Folder> folders;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<UserEmail> userEmails;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<TrashBinUserEmail> trashBinUserEmails;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private List<EmailOpeningOrder> emailOpeningOrders;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Contact> contacts;

    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER)
    private TrashBin trashBin;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(email);
    }
}
