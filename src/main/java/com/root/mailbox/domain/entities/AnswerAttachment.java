package com.root.mailbox.domain.entities;


import com.root.mailbox.domain.entities.keys.AnswerAttachmentKey;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "tb_answer_attachment")
@Entity
public class AnswerAttachment {
    @EmbeddedId
    private AnswerAttachmentKey answerAttachmentKey = new AnswerAttachmentKey();

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("answerId")
    @JoinColumn(name = "AA_ANSWER_ID")
    private Answer answer;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("attachmentId")
    @JoinColumn(name = "AA_ATTACHMENT_ID")
    private Attachment attachment;

    @CreationTimestamp
    @Column(name = "AA_CREATED_AT")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "AA_UPDATED_AT")
    private Date updatedAt;
}
