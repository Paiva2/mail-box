package com.root.mailbox.presentation.dto.email;

import com.root.mailbox.presentation.dto.user.GetUserProfileOutputDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CarbonCopyOutputDTO {
    private UUID id;
    private GetUserProfileOutputDTO user;
}
