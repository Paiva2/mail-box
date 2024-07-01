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
@Table(name = "tb_attachments")
@Entity
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "AT_ID")
    private UUID id;

    @Column(name = "AT_URL", unique = false, nullable = false)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "AT_EXTENSION", unique = false, nullable = false)
    private FileExtension extension;

    @Column(name = "AT_FILE_NAME", unique = false, nullable = false)
    private String fileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AT_EMAIL_ID")
    private Email email;

    @CreationTimestamp
    @Column(name = "AT_CREATED_AT")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "AT_UPDATED_AT")
    private Date updatedAt;

    public enum FileExtension {
        PDF,
        JPEG,
        JPG,
        XLSX,
        XLS,
        CSV,
        TXT;

        public String getExtension() {
            return this.name();
        }
    }
}
