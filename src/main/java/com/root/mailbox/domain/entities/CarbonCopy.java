package com.root.mailbox.domain.entities;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "tb_carbon_copies")
public class CarbonCopy {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "CC_ID")
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CC_EMAIL_ID", nullable = false)
    private Email email;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CC_USER_ID", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "CC_CREATED_AT")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "CC_UPDATED_AT")
    private Date updatedAt;
}
