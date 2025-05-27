package com.fioletowi.farma.resource;

import lombok.*;

import java.math.BigDecimal;

/**
 * Response DTO representing a summary of incomes and expenses.
 * Contains the total sum of incomes and total sum of expenses over a specified period or criteria.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseSummaryResponse {

    /**
     * Total sum of incomes (costs > 0).
     */
    private BigDecimal incomeSum;

    /**
     * Total sum of expenses (costs < 0).
     */
    private BigDecimal expenseSum;
}
