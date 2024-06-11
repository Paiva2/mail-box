package com.root.mailbox.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "tb_email_opening_orders")
@Entity
public class EmailOpeningOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "EO_ID")
    private UUID id;

    @Column(name = "EO_ORDER", nullable = false)
    private Integer order;

    @Column(name = "EO_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private OpeningStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EO_EMAIL_ID")
    private Email email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EO_USER_ID")
    private User user;

    public enum OpeningStatus {
        NOT_OPENED,
        OPENED
    }
}
