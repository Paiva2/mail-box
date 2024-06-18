package com.root.mailbox.presentation.dto.email;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ListTrashEmailsOutputDTO {
    private Integer page;
    private Integer size;
    private Long totalItems;
    private String keyword;
    private Boolean opened;
    private Boolean spam;
    private List<EmailOutputDTO> emails;
}
