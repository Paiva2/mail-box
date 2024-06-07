package com.root.mailbox.domain.usecases.contacts;

import com.root.mailbox.domain.entities.Contact;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.exceptions.UserNotFoundException;
import com.root.mailbox.infra.providers.ContactDataProvider;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.presentation.dto.contact.ContactOutputDTO;
import com.root.mailbox.presentation.dto.contact.ListContactsPaginationInputDTO;
import com.root.mailbox.presentation.dto.contact.ListContactsPaginationOutputDTO;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ListContactsUsecase {
    private UserDataProvider userDataProvider;
    private ContactDataProvider contactDataProvider;

    public ListContactsPaginationOutputDTO exec(Long userId, ListContactsPaginationInputDTO dto) {
        User user = checkIfUserExists(userId);
        handlePaginationDefault(dto);

        Page<Contact> contacts = getContactsPagination(user.getId(), dto);

        return mountOutput(contacts);
    }

    private User checkIfUserExists(Long userId) {
        return userDataProvider.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private void handlePaginationDefault(ListContactsPaginationInputDTO dto) {
        if (dto.getPage() < 1) {
            dto.setPage(1);
        }

        if (dto.getSize() < 5) {
            dto.setSize(5);
        } else if (dto.getSize() > 50) {
            dto.setSize(50);
        }
    }

    private Page<Contact> getContactsPagination(Long userId, ListContactsPaginationInputDTO dto) {
        Pageable pageable = PageRequest.of(dto.getPage() - 1, dto.getSize(), Sort.Direction.DESC, "CT_CREATED_AT");

        return contactDataProvider.findAllByUserId(userId, dto.getName(), pageable);
    }

    private ListContactsPaginationOutputDTO mountOutput(Page<Contact> contacts) {
        return ListContactsPaginationOutputDTO.builder()
            .page(contacts.getNumber() + 1)
            .size(contacts.getSize())
            .itemsPerPage(contacts.getTotalElements())
            .contacts(contacts.stream().map(contact -> ContactOutputDTO.builder()
                    .id(contact.getId())
                    .name(contact.getName())
                    .email(contact.getEmail())
                    .createdAt(contact.getCreatedAt())
                    .build()
                ).toList()
            ).build();
    }
}
