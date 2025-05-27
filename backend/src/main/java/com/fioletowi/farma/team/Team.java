package com.fioletowi.farma.team;

import com.fioletowi.farma.task.Task;
import com.fioletowi.farma.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a Team.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "team")
public class Team {

    /**
     * Unique identifier for the team.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the team.
     */
    @Column(nullable = false)
    private String name;

    /**
     * The leader of the team.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id", unique = false)
    private User leader;

    /**
     * Members of the team.
     */
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamMember> teamMembers = new ArrayList<>();

    /**
     * Tasks assigned to the team.
     */
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    /**
     * Timestamp when the team was created.
     */
    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    /**
     * Timestamp when the team was last updated.
     */
    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

}
