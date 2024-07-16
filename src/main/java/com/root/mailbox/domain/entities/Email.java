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

    @Column(name = "EM_MESSAGE", nullable = false, length = 1000)
    private String message;

    @Column(name = "EM_OPENING_ORDERS", nullable = false)
    private Boolean openingOrders;

    @Enumerated(EnumType.STRING)
    @Column(name = "EM_EMAIL_STATUS", nullable = false)
    private EmailStatus emailStatus;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EM_USER_ID")
    private User user;

    @OneToMany(mappedBy = "email", fetch = FetchType.LAZY)
    private List<UserEmail> usersEmails;

    @OneToMany(mappedBy = "email", fetch = FetchType.LAZY)
    private List<EmailOpeningOrder> emailOpeningOrders;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "email")
    private List<TrashBinUserEmail> trashBinUserEmails;

    @OneToMany(mappedBy = "email", fetch = FetchType.LAZY)
    private List<EmailAttachment> emailAttachments;

    @OneToMany(mappedBy = "email", fetch = FetchType.LAZY)
    private List<Answer> answers;

    public enum EmailStatus {
        DRAFT,
        SENT
    }
}
