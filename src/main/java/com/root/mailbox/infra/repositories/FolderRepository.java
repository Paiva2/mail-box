package com.root.mailbox.infra.repositories;

import com.root.mailbox.domain.entities.Folder;
import com.root.mailbox.domain.entities.dto.FolderDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {

    @Query("SELECT f FROM Folder f " +
        "JOIN FETCH f.user u " +
        "WHERE u.id = :userId " +
        "AND f.name = :name " +
        "AND (f.disabled = false AND f.disabledAt = null) " +
        "AND f.parentFolder = null")
    Optional<Folder> findByUserAndNameInRoot(@Param("userId") Long userId, @Param("name") String name);

    @Query("SELECT f FROM Folder f " +
        "JOIN FETCH f.user u " +
        "JOIN FETCH f.parentFolder pf " +
        "WHERE u.id = :userId " +
        "AND f.name = :name " +
        "AND pf.id = :parentFolderId " +
        "AND (f.disabled = false AND f.disabledAt = null)")
    Optional<Folder> findByUserAndNameInParent(@Param("userId") Long userId, @Param("name") String name, @Param("parentFolderId") Long parentFolderId);

    @Query(value = """
            SELECT new com.root.mailbox.domain.entities.dto.FolderDTO(
                f.id,
                f.name,
                    (
                        (SELECT COUNT(chd) > 0 FROM Folder chd
                        JOIN chd.parentFolder pf
                        WHERE pf.id = f.id
                        AND (chd.disabled = false AND chd.disabledAt = null))
                    )
                ,
                f.disabled,
                f.createdAt
            )
            FROM Folder f
            JOIN f.user usr
            WHERE usr.id = :userId
            AND (f.disabled = false AND f.disabledAt = null)
            AND f.parentFolder = null
            ORDER by f.createdAt DESC
        """)
    List<FolderDTO> findAllRootByUserId(@Param("userId") Long userId);

    @Query(value = """
            SELECT new com.root.mailbox.domain.entities.dto.FolderDTO(
                f.id,
                f.name,
                    (
                        (SELECT COUNT(chd) > 0 FROM Folder chd
                        JOIN chd.parentFolder pf
                        WHERE pf.id = f.id
                        AND (chd.disabled = false AND chd.disabledAt = null))
                    )
                ,
                f.disabled,
                f.createdAt
            )
            FROM Folder f
            JOIN f.user usr
            WHERE usr.id = :userId
            AND (f.disabled = false AND f.disabledAt = null)
            AND f.parentFolder.id = :parentFolderId
            ORDER by f.createdAt DESC
        """)
    List<FolderDTO> findAllChildrenByFolderIdAndUserId(@Param("userId") Long userId, @Param("parentFolderId") Long parentFolderId);
}
