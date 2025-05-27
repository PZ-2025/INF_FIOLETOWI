package com.fioletowi.farma.resource;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO for creating a new Expense.
 *
 * <p>This class contains the necessary fields to create a new expense entry,
 * including validation constraints to ensure data integrity.</p>
 *
 * <ul>
 *     <li><b>name</b>: The name of the expense. Must not be blank.</li>
 *     <li><b>description</b>: Optional description of the expense.</li>
 *     <li><b>resourceId</b>: The ID of the associated resource. Must not be null.</li>
 *     <li><b>quantity</b>: The quantity of items or units. Must be positive.</li>
 *     <li><b>cost</b>: The cost of the expense. Must be greater than zero.</li>
 * </ul>
 *
 * @author
 */
@Getter
@Setter
@Builder
public class NewExpenseRequest {

    @NotBlank(message = "Expense name is required")
    private String name;

    private String description;

    @NotNull(message = "Resource ID is required")
    private Long resourceId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private BigDecimal  quantity;

    @NotNull(message = "Cost is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Cost must be greater than zero")
    private BigDecimal cost;
}
