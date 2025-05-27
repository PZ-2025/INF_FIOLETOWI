package com.fioletowi.farma.resource;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object representing an expense response.
 * Contains details about an expense including its name, description,
 * associated resource, quantity, cost, and creation timestamp.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponse {
    /** Unique identifier of the expense */
    private Long id;

    /** Name of the expense */
    private String name;

    /** Description of the expense */
    private String description;

    /** Resource associated with the expense */
    private ResourceResponse resource;

    /** Quantity of the resource involved in the expense */
    private BigDecimal  quantity;

    /** Cost of the expense */
    private BigDecimal cost;

    /** Timestamp when the expense was created */
    private LocalDateTime createdAt;
}
