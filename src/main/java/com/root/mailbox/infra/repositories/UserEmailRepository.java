package com.root.mailbox.infra.repositories;

import com.root.mailbox.domain.entities.UserEmail;
import com.root.mailbox.domain.entities.keys.UserEmailKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserEmailRepository extends JpaRepository<UserEmail, UserEmailKey> {
    @Query(nativeQuery = true, value = "SELECT * FROM tb_users_emails ue " +
        "JOIN tb_users u ON ue.UM_USER_ID = u.USR_ID " +
        "JOIN tb_emails e ON e.EM_ID = ue.UM_EMAIL_ID " +
        "WHERE ue.UM_USER_ID = :userId " +
        "AND (ue.UM_DISABLED IS FALSE AND ue.UM_DELETED_AT IS NULL) " +
        "AND (ue.UM_EMAIL_TYPE = 'RECEIVED' OR ue.UM_EMAIL_TYPE = 'IN_COPY') " +
        "AND (u.USR_DISABLED IS FALSE AND u.USR_DISABLED_AT IS NULL) " +
        "AND (:filteringSpam IS NULL OR ue.UM_IS_SPAM = :filteringSpam) " +
        "AND (:opened IS NULL OR ue.UM_OPENED = :opened) " +
        "AND ue.UM_EMAIL_FLAG = :#{#flag.toString()} " +
        "AND ( :keyword IS NULL OR LOWER(e.EM_SUBJECT) LIKE CONCAT('%', LOWER(:keyword), '%') ) " +
        "AND ue.UM_FOLDER_ID IS NULL")
    Page<UserEmail> findAllReceivedByUserIdFilter(
        @Param("userId") Long userId,
        @Param("keyword") String keyword,
        @Param("filteringSpam") Boolean filteringSpam,
        @Param("opened") Boolean opened,
        @Param("flag") UserEmail.EmailFlag flag,
        Pageable pageable
    );

    @Query("SELECT ue FROM UserEmail ue " +
        "JOIN FETCH ue.user u " +
        "JOIN FETCH ue.email e " +
        "WHERE e.id = :emailId " +
        "AND u.id = :userId " +
        "AND ue.deletedAt = null")
    Optional<UserEmail> findByUserIdAndEmailId(@Param("userId") Long userId, @Param("emailId") UUID emailId);

    @Modifying
    @Query("UPDATE UserEmail ue SET ue.opened = true WHERE ue.email.id = :emailId AND ue.user.id = :userId")
    void markOpened(@Param("userId") Long userId, @Param("emailId") UUID emailId);

    @Query(nativeQuery = true, value = """
        SELECT * FROM tb_users_emails ue
        JOIN tb_emails em ON em.EM_ID = ue.UM_EMAIL_ID
        WHERE ue.UM_USER_ID = :userId
        AND ue.UM_EMAIL_ID IN (
            SELECT UTE_EMAIL_ID FROM tb_trash_bin_users_emails
            WHERE UTE_USER_ID = :userId AND UTE_TRASH_BIN_ID = :trashId
        )
        AND (ue.UM_DISABLED IS TRUE AND ue.UM_DELETED_AT IS NULL)
        AND (:keyword IS NULL OR LOWER(em.EM_SUBJECT) LIKE CONCAT('%', LOWER(:keyword), '%'))
        AND (:opened IS NULL OR ue.UM_OPENED = :opened)
        AND (:spam IS NULL OR ue.UM_IS_SPAM = :spam)
        """)
    Page<UserEmail> findAllInTrashByUser(@Param("userId") Long userId, @Param("trashId") UUID trashId, @Param("keyword") String keyword, @Param("opened") Boolean opened, @Param("spam") Boolean spam, Pageable pageable);

    @Query(nativeQuery = true, value = """
        SELECT * FROM tb_users_emails um
        JOIN tb_folders fd ON fd.FD_ID = um.UM_FOLDER_ID
        JOIN tb_users usr ON usr.USR_ID = um.UM_USER_ID
        JOIN tb_emails em ON em.EM_ID = um.UM_EMAIL_ID
        WHERE um.UM_USER_ID = :userId
        AND um.UM_FOLDER_ID = :folderId
        AND (um.UM_DISABLED IS FALSE AND um.UM_DELETED_AT IS NULL)
        AND ( :keyword IS NULL OR LOWER(em.EM_SUBJECT) LIKE CONCAT('%', LOWER(:keyword),'%') )
        """)
    Page<UserEmail> findAllByUserIdAndFolderId(@Param("userId") Long userId, @Param("folderId") Long folderId, Pageable pageable, @Param("keyword") String keyword);

    @Query(nativeQuery = true, value = """
        SELECT * FROM tb_users_emails um
        JOIN tb_emails em ON em.EM_ID = um.UM_EMAIL_ID
        WHERE um.UM_USER_ID = :userId
        AND (um.UM_DISABLED IS FALSE AND um.UM_DELETED_AT IS NULL)
        AND (um.UM_EMAIL_TYPE = 'MINE' AND em.EM_EMAIL_STATUS = 'DRAFT')
        AND ( :keyword IS NULL OR LOWER(em.EM_SUBJECT) LIKE CONCAT('%', LOWER(:keyword), '%') )
        """)
    Page<UserEmail> findAllDraftsByUser(@Param("userId") Long userId, @Param("keyword") String keyword, Pageable pageable);

    @Query(nativeQuery = true, value = """
        SELECT * FROM tb_users_emails um
        JOIN tb_emails em ON em.EM_ID = um.UM_EMAIL_ID
        WHERE um.UM_USER_ID = :userId
        AND (um.UM_DISABLED IS FALSE AND um.UM_DELETED_AT IS NULL)
        AND um.UM_EMAIL_TYPE = 'MINE'
        AND um.UM_IS_SPAM IS FALSE
        AND ( :keyword IS NULL OR LOWER(em.EM_SUBJECT) LIKE CONCAT('%', LOWER(:keyword), '%') )
        """)
    Page<UserEmail> findAllByUserSentFiltering(@Param("userId") Long userId, @Param("keyword") String keyword, Pageable pageable);
}