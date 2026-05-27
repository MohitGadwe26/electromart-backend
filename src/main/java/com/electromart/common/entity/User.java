package com.electromart.common.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.electromart.address.entity.Address;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ================= BASIC INFO ================= */

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String phone;
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    @Builder.Default
    private boolean enabled = false;

    @Builder.Default
    private boolean deleted = false;

    /* ================= TIMESTAMPS ================= */

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /* ================= STATUS AUDIT FIELDS ================= */

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private Long deletedBy;

    @Column(name = "reactivated_at")
    private LocalDateTime reactivatedAt;

    @Column(name = "reactivated_by")
    private Long reactivatedBy;

    @Column(name = "disabled_at")
    private LocalDateTime disabledAt;

    @Column(name = "disabled_by")
    private Long disabledBy;

    @Column(name = "last_status_change_at")
    private LocalDateTime lastStatusChangeAt;

    @Column(name = "last_status_change_by")
    private Long lastStatusChangeBy;

    @Column(name = "status_change_reason", length = 500)
    private String statusChangeReason;

    /* ================= LOGIN TRACKING ================= */

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "login_count")
    @Builder.Default
    private Integer loginCount = 0;

    /* ================= SECURITY / TOKENS ================= */

    private String passwordResetToken;

    @Column(name = "reset_token_expiry")
    private LocalDateTime passwordResetTokenExpiry;

    private String emailVerificationToken;

    @Column(name = "verification_token_expiry")
    private LocalDateTime emailVerificationExpiry;

    private String reactivationToken;

    @Column(name = "reactivation_token_expiry")
    private LocalDateTime reactivationTokenExpiry;

    /* ================= RELATIONSHIPS ================= */

    @OneToMany(
        mappedBy = "user",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<Address> addresses = new ArrayList<>();

    /* =====================================================
       DOMAIN METHODS
       ===================================================== */

    public void softDelete(Long adminId) {
        //deleted = true;
        deleteUser(adminId, "Soft deleted by admin");
       
    }

    public void deleteUser(Long adminId, String reason) {
        this.enabled = false;
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = adminId;

        this.lastStatusChangeAt = LocalDateTime.now();
        this.lastStatusChangeBy = adminId;
        this.statusChangeReason = reason;
        this.updatedAt = LocalDateTime.now();
    }

    public void restoreUser(Long adminId, String reason) {
        this.deleted = false;
        this.deletedAt = null;
        this.deletedBy = null;

        this.enabled = true;
        this.reactivatedAt = LocalDateTime.now();
        this.reactivatedBy = adminId;

        this.lastStatusChangeAt = LocalDateTime.now();
        this.lastStatusChangeBy = adminId;
        this.statusChangeReason = reason;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * ✅ REQUIRED BY UserAdminFacade
     * Simple restore wrapper
     */
    public void restore(Long adminId) {
        restoreUser(adminId, "User restored by admin");
    }

    public void enableUser(Long adminId, String reason) {
        this.enabled = true;
        this.reactivatedAt = LocalDateTime.now();
        this.reactivatedBy = adminId;

        if (this.deleted) {
            this.deleted = false;
            this.deletedAt = null;
            this.deletedBy = null;
        }

        this.lastStatusChangeAt = LocalDateTime.now();
        this.lastStatusChangeBy = adminId;
        this.statusChangeReason = reason;
        this.updatedAt = LocalDateTime.now();
    }

    public void disableUser(Long adminId, String reason) {
        this.enabled = false;
        this.disabledAt = LocalDateTime.now();
        this.disabledBy = adminId;

        this.lastStatusChangeAt = LocalDateTime.now();
        this.lastStatusChangeBy = adminId;
        this.statusChangeReason = reason;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Existing method (kept as-is)
     */
    public void updateStatusChange(Long adminId,
                                   Boolean enabled,
                                   Boolean deleted) {
        updateStatusChange(adminId, null, enabled, deleted);
    }

    /**
     * ✅ NEW OVERLOAD (REQUIRED BY FACADE)
     */
    public void updateStatusChange(Long adminId,
                                   String reason,
                                   Boolean enabled,
                                   Boolean deleted) {

        if (enabled != null) {
            if (enabled) {
                enableUser(adminId, reason);
            } else {
                disableUser(adminId, reason);
            }
        }

        if (deleted != null && deleted) {
            deleteUser(adminId, reason);
        }

        this.lastStatusChangeAt = LocalDateTime.now();
        this.lastStatusChangeBy = adminId;
        this.statusChangeReason = reason;
        this.updatedAt = LocalDateTime.now();
    }

    /* ================= LOGIN ================= */

    public void recordLogin() {
        this.lastLoginAt = LocalDateTime.now();
        this.loginCount = (this.loginCount == null) ? 1 : this.loginCount + 1;
        this.updatedAt = LocalDateTime.now();
    }

    /* ================= QUERY HELPERS ================= */

    public boolean isActive() {
        return this.enabled && !this.deleted;
    }

    public String getStatus() {
        if (this.deleted) return "DELETED";
        if (!this.enabled) return "DISABLED";
        return "ACTIVE";
    }
}
