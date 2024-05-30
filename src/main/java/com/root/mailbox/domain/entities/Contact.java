package com.root.mailbox.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tb_contacts")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "CT_ID")
    private Long id;

    @Column(name = "CT_NAME", nullable = false)
    private String name;

    @Column(name = "CT_EMAIL", nullable = false)
    private String email;

    @CreationTimestamp
    @Column(name = "CT_CREATED_AT")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "CT_UPDATED_AT")
    private Date updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CT_USER_ID")
    private User user;
}
