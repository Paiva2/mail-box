package com.root.mailbox.infra.providers;

import com.root.mailbox.domain.entities.EmailOpeningOrder;
import com.root.mailbox.infra.repositories.EmailOpeningOrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class EmailOpeningOrderDataProvider {
    private final EmailOpeningOrderRepository emailOpeningOrderRepository;

    public List<EmailOpeningOrder> createEmailOrders(List<EmailOpeningOrder> emailOpeningOrders) {
        return emailOpeningOrderRepository.saveAll(emailOpeningOrders);
    }
}
