package com.fioletowi.farma.team;

import com.fioletowi.farma.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing a member of a team.
 * Each TeamMember links a User to a Team.
 * Uniqueness is ensured by the combination of team and user.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "team_members", uniqueConstraints = {@UniqueConstraint(columnNames = {"team_id", "user_id"})})
public class TeamMember {

    /**
     * Primary key identifier for the team member record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The team to which the user belongs.
     * Cascade delete is applied, so removing a team removes its members.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Team team;

    /**
     * The user who is a member of the team.
     * Cascade delete is applied, so removing a user removes their team memberships.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    /**
     * Timestamp marking when the membership was created.
     * Not updatable once set.
     */
    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

}
