package com.root.mailbox.infra.repositories;

import com.root.mailbox.domain.entities.CarbonCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CarbonCopyRepository extends JpaRepository<CarbonCopy, UUID> {
}
