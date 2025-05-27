package com.fioletowi.farma.resource;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing an expense record.
 * <p>
 * Contains information about the expense name, description, associated resource,
 * quantity, cost, and creation timestamp.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "expenses")
public class Expense {

    /**
     * Unique identifier of the expense.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name or title of the expense.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Optional description providing more details about the expense.
     */
    @Column(name = "desciprion")
    private String description;

    /**
     * The resource associated with this expense.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;

    /**
     * Quantity of the resource involved in this expense.
     */
    private BigDecimal  quantity;

    /**
     * Cost value of the expense.
     */
    private BigDecimal cost;

    /**
     * Timestamp when the expense record was created.
     * Automatically populated and not updatable.
     */
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
