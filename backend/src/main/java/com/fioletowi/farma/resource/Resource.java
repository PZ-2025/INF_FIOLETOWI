package com.fioletowi.farma.resource;

import com.fioletowi.farma.task.TaskResource;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Resource entity in the system.
 *
 * <p>A resource has a unique ID, name, quantity, type, and can be associated
 * with multiple expenses. The creation timestamp is automatically set upon persistence.</p>
 *
 * <ul>
 *   <li><b>id</b> - Unique identifier of the resource.</li>
 *   <li><b>name</b> - Name of the resource (not nullable).</li>
 *   <li><b>quantity</b> - Available quantity of the resource.</li>
 *   <li><b>resourceType</b> - Enum representing the type of the resource.</li>
 *   <li><b>expenses</b> - List of associated expenses related to this resource.</li>
 *   <li><b>createdAt</b> - Timestamp when the resource was created (set automatically).</li>
 * </ul>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "resource")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private BigDecimal quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false)
    private ResourceType resourceType;

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Expense> expenses = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

}
