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
@Entity
@Table(name = "tb_trash_bin")
public class TrashBin {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "TR_ID")
    private UUID id;

    @CreationTimestamp
    @Column(name = "TR_CREATED_AT")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "TR_UPDATED_AT")
    private Date updatedAt;

    @OneToOne
    @JoinColumn(name = "TR_USER_ID")
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "trashBin")
    private List<TrashBinUserEmail> trashBinUserEmails;
}
