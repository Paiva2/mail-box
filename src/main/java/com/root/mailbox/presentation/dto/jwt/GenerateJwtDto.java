package com.root.mailbox.presentation.dto.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class GenerateJwtDto {
    private Long id;
    private String role;
}
