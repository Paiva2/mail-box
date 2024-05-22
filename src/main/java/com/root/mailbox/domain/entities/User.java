package com.root.mailbox.domain.entities;

import com.root.mailbox.domain.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Table(name = "tb_users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User {
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
}
