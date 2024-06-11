package com.root.mailbox.infra.repositories;

import com.root.mailbox.domain.entities.EmailOpeningOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmailOpeningOrderRepository extends JpaRepository<EmailOpeningOrder, UUID> {
    List<EmailOpeningOrder> findAllByEmailId(UUID emailId);
}
