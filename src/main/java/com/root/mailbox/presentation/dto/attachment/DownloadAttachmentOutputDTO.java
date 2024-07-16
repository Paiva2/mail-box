package com.root.mailbox.presentation.dto.attachment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DownloadAttachmentOutputDTO {
    String contentType;
    String originalFileName;
    byte[] fileContentBytes;
}
