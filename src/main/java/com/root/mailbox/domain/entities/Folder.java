package com.root.mailbox.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "tb_folders")
@Entity
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "FD_ID")
    private Long id;

    @Column(name = "FD_NAME", nullable = false)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "folder")
    private List<UserEmail> userEmails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FD_PARENT_FOLDER_ID", nullable = true)
    private Folder parentFolder;

    @Column(name = "FD_DISABLED", nullable = true)
    private Boolean disabled = false;

    @Column(name = "FD_DISABLED_AT", nullable = true)
    private Date disabledAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FD_USER_ID")
    private User user;

    @CreationTimestamp
    @Column(name = "FD_CREATED_AT")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "FD_UPDATED_AT")
    private Date updatedAt;
}
