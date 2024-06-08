package com.root.mailbox.infra.providers;

import com.root.mailbox.infra.repositories.EmailOpeningOrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EmailOpeningOrderDataProvider {
    private final EmailOpeningOrderRepository emailOpeningOrderRepository;
}
