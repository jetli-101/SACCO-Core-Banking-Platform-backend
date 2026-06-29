package com.example.sacco_core_banking.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * One row per user-group membership. Explicit join entity (same reasoning as
 * UserRole) so membership has its own identity and can be managed independently of
 * editing a User or a UserGroup.
 */
@Entity
@Table(name = "smoothsurf_sacco_user_group_members")
@Getter
@Setter
@NoArgsConstructor
public class UserGroupMember extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_group_id", nullable = false)
    private UserGroup userGroup;

    public UserGroupMember(User user, UserGroup userGroup) {
        this.user = user;
        this.userGroup = userGroup;
    }
}
