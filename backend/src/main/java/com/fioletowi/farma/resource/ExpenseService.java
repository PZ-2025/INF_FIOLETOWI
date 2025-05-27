package com.fioletowi.farma.resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

/**
 * Service interface for managing expenses.
 * Provides methods to retrieve, create, update, delete expenses,
 * as well as fetch expense summaries and filtered expense lists.
 */
public interface ExpenseService {

    /**
     * Retrieves a paginated list of all expenses.
     *
     * @param pageable pagination and sorting information
     * @return paginated list of ExpenseResponse
     */
    Page<ExpenseResponse> findAllExpenses(Pageable pageable);

    /**
     * Retrieves a specific expense by its ID.
     *
     * @param id the ID of the expense
     * @return the ExpenseResponse for the given ID
     */
    ExpenseResponse findExpenseById(Long id);

    /**
     * Creates a new expense based on the provided request.
     *
     * @param newExpenseRequest request containing expense creation data
     * @return the created ExpenseResponse
     */
    ExpenseResponse createExpense(NewExpenseRequest newExpenseRequest);

    /**
     * Partially updates an existing expense identified by ID.
     *
     * @param id the ID of the expense to update
     * @param updateExpenseRequest request containing fields to update
     * @return the updated ExpenseResponse
     */
    ExpenseResponse partialUpdateExpense(Long id, UpdateExpenseRequest updateExpenseRequest);

    /**
     * Deletes an expense by its ID.
     *
     * @param id the ID of the expense to delete
     */
    void deleteExpense(Long id);

    /**
     * Retrieves a summary of expenses and incomes within the specified date range and filtered by resource.
     *
     * @param from start of the date range (inclusive)
     * @param to end of the date range (inclusive)
     * @param resource resource name to filter by, or "all" for no filtering
     * @return summary of expenses as ExpenseSummaryResponse
     */
    ExpenseSummaryResponse getExpenseSummary(LocalDateTime from, LocalDateTime to, String resource);

    /**
     * Retrieves a paginated list of expenses filtered by date range, type, and resource.
     *
     * @param from start of the date range (inclusive)
     * @param to end of the date range (inclusive)
     * @param type expense type filter (e.g., "income", "expense", or "all")
     * @param resource resource name filter, or "all" for no filtering
     * @param pageable pagination and sorting information
     * @return paginated list of ExpenseResponse matching the filters
     */
    Page<ExpenseResponse> findExpensesByDateRange(LocalDateTime from, LocalDateTime to, String type, String resource, Pageable pageable);
}
