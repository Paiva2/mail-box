package com.root.mailbox.infra.repositories;

import com.root.mailbox.domain.entities.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {

    @Query("SELECT f FROM Folder f " +
        "JOIN FETCH f.user u " +
        "WHERE u.id = :userId " +
        "AND f.name = :name " +
        "AND (f.disabled = false AND f.disabledAt = null) ")
    Optional<Folder> findByUserIdAndName(@Param("userId") Long userId, @Param("name") String name);
}
