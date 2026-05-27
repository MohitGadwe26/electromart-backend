package com.electromart.user.repository;

import com.electromart.common.entity.Role;
import com.electromart.common.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Existing methods
    Optional<User> findByEmail(String email);
    List<User> findByDeletedFalse();
    boolean existsByEmail(String email);

    // New methods for profile management
    Optional<User> findByEmailAndDeletedFalse(String email);
    //boolean existsByEmailAndDeletedFalse(String email);
    // For reactivation - find user even if disabled (but not deleted)
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.deleted = false")
    Optional<User> findByEmailIncludingDisabled(@Param("email") String email);

    Optional<User> findByIdAndDeletedFalse(Long id);
    List<User> findByIdInAndDeletedFalse(List<Long> ids);

     // For email verification
    Optional<User> findByEmailVerificationToken(String token);

    // For password reset
    Optional<User> findByPasswordResetToken(String token);

     // Admin methods
    Page<User> findByDeletedFalse(Pageable pageable);
    Page<User> findByRoleAndDeletedFalse(Role role, Pageable pageable);
    Page<User> findByEnabledAndDeletedFalse(boolean enabled, Pageable pageable);

    @Query("SELECT u FROM User u WHERE " +
        "(LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
        "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
        "u.phone LIKE CONCAT('%', :query, '%')) AND " +
        "u.deleted = false")
    Page<User> searchUsers(@Param("query") String query, Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE u.deleted = false")
    long countActiveUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.deleted = false AND u.enabled = true")
    long countEnabledUsers();

    @Query("SELECT u FROM User u WHERE u.deleted = false AND u.updatedAt >= :since")
    List<User> findRecentlyUpdated(@Param("since") LocalDateTime since);

    @Query("SELECT COUNT(u) FROM User u WHERE u.deleted = false AND u.createdAt >= :since")
    long countCreatedSince(@Param("since") LocalDateTime since);

    @Query("SELECT u FROM User u WHERE u.id IN :ids AND u.deleted = :deleted AND u.enabled = :enabled")
    List<User> findByIdInAndStatus(@Param("ids") List<Long> ids, 
        @Param("deleted") boolean deleted,
        @Param("enabled") boolean enabled);
    
    // Find super admins count
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'SUPER_ADMIN' AND u.deleted = false")
    long countSuperAdmins();

    @Modifying
    @Query("UPDATE User u SET u.enabled = :enabled WHERE u.id = :id")
    void updateUserStatus(@Param("id") Long id, @Param("enabled") boolean enabled);

    @Query("SELECT u FROM User u WHERE u.deleted = false ORDER BY u.createdAt DESC")
    List<User> findRecentUsers(@Param("limit") int limit);


}
