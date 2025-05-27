package com.fioletowi.farma.task;

import com.fioletowi.farma.resource.Resource;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing the resource associated with a task.
 *
 * Each TaskResource links a {@link Task} with a {@link Resource},
 * including details like the type of resource usage and quantity.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "task_resource")
public class TaskResource {

    /**
     * Unique identifier of the TaskResource.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The task this resource is assigned to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    /**
     * The resource associated with the task.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;

    /**
     * The type of the task resource, describing how the resource is used.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "task_resource_type", nullable = false)
    private TaskResourceType taskResourceType;

    /**
     * Quantity of the resource used or assigned.
     */
    @Column
    private BigDecimal quantity;

    /**
     * Timestamp when the resource assignment was created.
     */
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
