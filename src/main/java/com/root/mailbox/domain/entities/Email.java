package com.root.mailbox.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "tb_emails")
@Entity
public class Email implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "EM_ID")
    private UUID id;

    @Column(name = "EM_SUBJECT", nullable = false)
    private String subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EM_USER_TO_ID", nullable = false)
    private User userTo;

    @Column(name = "EM_MESSAGE", nullable = false)
    private String message;

    @Column(name = "EM_OPENED", nullable = false)
    private Boolean opened;

    @Column(name = "EM_IS_SPAM", nullable = false)
    private Boolean isSpam;

    @CreationTimestamp
    @Column(name = "EM_CREATED_AT")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "EM_UPDATED_AT")
    private Date updatedAt;

    @Column(name = "EM_DISABLED", nullable = true)
    private Boolean disabled = false;

    @Column(name = "EM_DISABLED_AT", nullable = true)
    private Date deletedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "email")
    private List<UserEmail> userEmails;

    @OneToMany(mappedBy = "email", fetch = FetchType.LAZY)
    private List<CarbonCopy> cCopies;
}
