package com.root.mailbox.domain.entities;


import com.root.mailbox.domain.entities.keys.EmailAttachmentKey;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Getter
@Setter
@Table(name = "tb_email_attachment")
@Entity
public class EmailAttachment {
    @EmbeddedId
    private EmailAttachmentKey emailAttachmentKey = new EmailAttachmentKey();

    public EmailAttachment() {
    }

    public EmailAttachment(Email email, Attachment attachment) {
        this.email = email;
        this.attachment = attachment;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("emailId")
    @JoinColumn(name = "EA_EMAIL_ID")
    private Email email;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("attachmentId")
    @JoinColumn(name = "EA_ATTACHMENT_ID")
    private Attachment attachment;

    @CreationTimestamp
    @Column(name = "EA_CREATED_AT")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "EA_UPDATED_AT")
    private Date updatedAt;
}
