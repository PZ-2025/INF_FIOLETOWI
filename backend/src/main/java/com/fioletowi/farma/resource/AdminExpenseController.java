package com.fioletowi.farma.resource;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * REST controller for managing expenses in the admin panel.
 * <p>
 * Provides endpoints to create, read, update, delete, and filter expenses.
 * Access is restricted to users with roles OWNER and MANAGER (some endpoints OWNER only).
 * </p>
 */
@RestController
@RequestMapping("/admin/expense")
@RequiredArgsConstructor
public class AdminExpenseController {

    private final ExpenseService expenseService;

    /**
     * Retrieves a paginated list of all expenses.
     *
     * @param pageable pagination information
     * @return paginated expenses
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public Page<ExpenseResponse> getAllExpenses(Pageable pageable) {
        return expenseService.findAllExpenses(pageable);
    }

    /**
     * Retrieves a single expense by its unique ID.
     *
     * @param id the expense ID
     * @return expense details wrapped in ResponseEntity
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ExpenseResponse> getExpenseById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(expenseService.findExpenseById(id));
    }

    /**
     * Creates a new expense entry.
     *
     * @param newExpenseRequest the request body containing expense details
     * @return created expense details wrapped in ResponseEntity
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ExpenseResponse> createExpense(@RequestBody @Valid NewExpenseRequest newExpenseRequest) {
        return ResponseEntity.ok(expenseService.createExpense(newExpenseRequest));
    }

    /**
     * Partially updates an existing expense by its ID.
     *
     * @param id the expense ID to update
     * @param updateExpenseRequest the partial update data
     * @return updated expense details wrapped in ResponseEntity
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ExpenseResponse> partialUpdateExpense(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateExpenseRequest updateExpenseRequest) {
        return ResponseEntity.ok(expenseService.partialUpdateExpense(id, updateExpenseRequest));
    }

    /**
     * Deletes an expense by its ID.
     * <p>
     * Only accessible to users with the OWNER role.
     * </p>
     *
     * @param id the expense ID to delete
     * @return ResponseEntity with no content status
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER')")
    public ResponseEntity<Void> deleteExpense(@PathVariable("id") Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves a summary of expenses for a given date range and optionally filtered by resource.
     *
     * @param startDate the start date (inclusive) in dd.MM.yyyy format
     * @param endDate the end date (inclusive) in dd.MM.yyyy format
     * @param resource resource filter, defaults to "all"
     * @return expense summary wrapped in ResponseEntity
     */
    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ExpenseSummaryResponse> getExpenseSummary(
            @RequestParam("startDate") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate endDate,
            @RequestParam(value = "resource", defaultValue = "all") String resource) {
        LocalDateTime from = startDate.atStartOfDay();
        LocalDateTime to = endDate.atTime(23, 59, 59);
        ExpenseSummaryResponse summary = expenseService.getExpenseSummary(from, to, resource);
        return ResponseEntity.ok(summary);
    }

    /**
     * Retrieves a paginated list of expenses filtered by date range, type, and resource.
     *
     * @param startDate the start date (inclusive) in dd.MM.yyyy format
     * @param endDate the end date (inclusive) in dd.MM.yyyy format
     * @param type expense type filter, defaults to "all"
     * @param resource resource filter, defaults to "all"
     * @param pageable pagination information
     * @return filtered and paginated expenses wrapped in ResponseEntity
     */
    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<Page<ExpenseResponse>> getExpensesByDateRange(
            @RequestParam("startDate") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate endDate,
            @RequestParam(value = "type", defaultValue = "all") String type,
            @RequestParam(value = "resource", defaultValue = "all") String resource,
            Pageable pageable) {
        LocalDateTime from = startDate.atStartOfDay();
        LocalDateTime to = endDate.atTime(23, 59, 59);
        Page<ExpenseResponse> expenses = expenseService.findExpensesByDateRange(from, to, type, resource, pageable);
        return ResponseEntity.ok(expenses);
    }

}
