package com.root.mailbox.presentation.dto.contact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ListContactsPaginationOutputDTO {
    private Integer page;
    private Integer size;
    private Long itemsPerPage;
    private List<ContactOutputDTO> contacts;
}
