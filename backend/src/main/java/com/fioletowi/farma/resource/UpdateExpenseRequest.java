package com.fioletowi.farma.resource;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO for updating an existing Expense.
 *
 * Fields are optional and can be partially updated.
 */
@Getter
@Setter
@Builder
public class UpdateExpenseRequest {
    /** Updated name of the expense. */
    private String name;

    /** Updated description of the expense. */
    private String description;

    /** Updated ID of the associated resource. */
    private Long resourceId;

    /** Updated quantity of the expense. */
    private BigDecimal  quantity;

    /** Updated cost of the expense. */
    private BigDecimal cost;
}
