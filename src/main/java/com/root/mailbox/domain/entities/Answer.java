package com.root.mailbox.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "tb_answers")
@Entity
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "AS_ID")
    private UUID id;

    @Column(name = "AS_MESSAGE", nullable = false, length = 1000)
    private String message;

    @CreationTimestamp
    @Column(name = "AS_CREATED_AT")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "AS_UPDATED_AT")
    private Date updatedAt;

    @Column(name = "AS_DISABLED", nullable = true)
    private Boolean disabled = false;

    @Column(name = "AS_DISABLED_AT", nullable = true)
    private Date deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AS_USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EM_EMAIL_ID")
    private Email email;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "answer")
    private List<AnswerAttachment> answerAttachments;
}
