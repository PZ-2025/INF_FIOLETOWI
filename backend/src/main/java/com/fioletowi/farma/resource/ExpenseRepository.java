package com.fioletowi.farma.resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Repository interface for managing {@link Expense} entities.
 * Provides methods to retrieve expenses, sums of incomes and expenses,
 * filtered by date ranges, cost, and associated resource name.
 */
@Repository
public interface ExpenseRepository extends CrudRepository<Expense, Long> {

    /**
     * Retrieves all expenses with pagination.
     *
     * @param pageable paging information
     * @return page of expenses
     */
    Page<Expense> findAll(Pageable pageable);

    /**
     * Calculates the total sum of income (cost > 0) for expenses
     * created between the given date range.
     *
     * @param from start date/time (inclusive)
     * @param to   end date/time (inclusive)
     * @return sum of incomes or 0 if none found
     */
    @Query("SELECT COALESCE(SUM(e.cost), 0) FROM Expense e WHERE e.createdAt BETWEEN :from AND :to AND e.cost > 0")
    BigDecimal sumIncomes(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    /**
     * Calculates the total sum of expenses (cost < 0) for expenses
     * created between the given date range.
     *
     * @param from start date/time (inclusive)
     * @param to   end date/time (inclusive)
     * @return sum of expenses or 0 if none found
     */
    @Query("SELECT COALESCE(SUM(e.cost), 0) FROM Expense e WHERE e.createdAt BETWEEN :from AND :to AND e.cost < 0")
    BigDecimal sumExpenses(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    /**
     * Retrieves all expenses created between the specified date range.
     *
     * @param from     start date/time (inclusive)
     * @param to       end date/time (inclusive)
     * @param pageable paging information
     * @return page of expenses in date range
     */
    Page<Expense> findAllByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    /**
     * Retrieves all income expenses (cost > 0) created between the specified date range.
     *
     * @param from     start date/time (inclusive)
     * @param to       end date/time (inclusive)
     * @param cost     cost threshold (should be 0)
     * @param pageable paging information
     * @return page of income expenses
     */
    Page<Expense> findAllByCreatedAtBetweenAndCostGreaterThan(LocalDateTime from, LocalDateTime to, BigDecimal cost, Pageable pageable);

    /**
     * Retrieves all expense records (cost < 0) created between the specified date range.
     *
     * @param from     start date/time (inclusive)
     * @param to       end date/time (inclusive)
     * @param cost     cost threshold (should be 0)
     * @param pageable paging information
     * @return page of expense records
     */
    Page<Expense> findAllByCreatedAtBetweenAndCostLessThan(LocalDateTime from, LocalDateTime to, BigDecimal cost, Pageable pageable);

    /**
     * Retrieves all income expenses (cost > 0) for a specific resource name
     * created between the specified date range.
     *
     * @param from         start date/time (inclusive)
     * @param to           end date/time (inclusive)
     * @param resourceName name of the resource
     * @param cost         cost threshold (should be 0)
     * @param pageable     paging information
     * @return page of income expenses for resource
     */
    Page<Expense> findAllByCreatedAtBetweenAndResourceNameAndCostGreaterThan(
            LocalDateTime from, LocalDateTime to, String resourceName, BigDecimal cost, Pageable pageable);

    /**
     * Retrieves all expense records (cost < 0) for a specific resource name
     * created between the specified date range.
     *
     * @param from         start date/time (inclusive)
     * @param to           end date/time (inclusive)
     * @param resourceName name of the resource
     * @param cost         cost threshold (should be 0)
     * @param pageable     paging information
     * @return page of expense records for resource
     */
    Page<Expense> findAllByCreatedAtBetweenAndResourceNameAndCostLessThan(
            LocalDateTime from, LocalDateTime to, String resourceName, BigDecimal cost, Pageable pageable);

    /**
     * Retrieves all expenses for a specific resource name created between the specified date range.
     *
     * @param from         start date/time (inclusive)
     * @param to           end date/time (inclusive)
     * @param resourceName name of the resource
     * @param pageable     paging information
     * @return page of expenses for resource
     */
    @Query("SELECT e FROM Expense e WHERE e.createdAt BETWEEN :from AND :to AND e.resource.name = :resourceName")
    Page<Expense> findAllByCreatedAtBetweenAndResourceName(@Param("from") LocalDateTime from,
                                                           @Param("to") LocalDateTime to,
                                                           @Param("resourceName") String resourceName,
                                                           Pageable pageable);

    /**
     * Calculates the total sum of incomes (cost > 0) for a specific resource name
     * created between the given date range.
     *
     * @param from         start date/time (inclusive)
     * @param to           end date/time (inclusive)
     * @param resourceName name of the resource
     * @return sum of incomes or 0 if none found
     */
    @Query("SELECT COALESCE(SUM(e.cost), 0) FROM Expense e WHERE e.createdAt BETWEEN :from AND :to AND e.resource.name = :resourceName AND e.cost > 0")
    BigDecimal sumIncomesByResourceName(@Param("from") LocalDateTime from,
                                        @Param("to") LocalDateTime to,
                                        @Param("resourceName") String resourceName);

    /**
     * Calculates the total sum of expenses (cost < 0) for a specific resource name
     * created between the given date range.
     *
     * @param from         start date/time (inclusive)
     * @param to           end date/time (inclusive)
     * @param resourceName name of the resource
     * @return sum of expenses or 0 if none found
     */
    @Query("SELECT COALESCE(SUM(e.cost), 0) FROM Expense e WHERE e.createdAt BETWEEN :from AND :to AND e.resource.name = :resourceName AND e.cost < 0")
    BigDecimal sumExpensesByResourceName(@Param("from") LocalDateTime from,
                                         @Param("to") LocalDateTime to,
                                         @Param("resourceName") String resourceName);

}
