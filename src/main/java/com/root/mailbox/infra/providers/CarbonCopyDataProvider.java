package com.root.mailbox.infra.providers;

import com.root.mailbox.domain.entities.CarbonCopy;
import com.root.mailbox.infra.repositories.CarbonCopyRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class CarbonCopyDataProvider {
    private CarbonCopyRepository carbonCopyRepository;

    public List<CarbonCopy> saveAllCopies(List<CarbonCopy> carbonCopies) {
        return carbonCopyRepository.saveAll(carbonCopies);
    }
}
