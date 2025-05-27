package com.fioletowi.farma.resource;

import com.fioletowi.farma.exception.ResourceNotFoundException;
import com.fioletowi.farma.mapper.Mapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Implementation of {@link ExpenseService} for managing expenses.
 * Provides CRUD operations, filtering, and summary calculations.
 */
@Service
@AllArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ResourceRepository resourceRepository;
    private final Mapper<Expense, ExpenseResponse> expenseMapper;
    private final Mapper<Resource, ResourceResponse> resourceMapper;

    /**
     * Retrieves a paginated list of all expenses.
     *
     * @param pageable pagination and sorting information
     * @return paginated list of mapped {@link ExpenseResponse} objects
     */
    @Override
    public Page<ExpenseResponse> findAllExpenses(Pageable pageable) {
        return expenseRepository.findAll(pageable)
                .map(expense -> {
                    ExpenseResponse response = expenseMapper.mapTo(expense, ExpenseResponse.class);
                    response.setResource(resourceMapper.mapTo(expense.getResource(), ResourceResponse.class));
                    return response;
                });
    }

    /**
     * Finds an expense by its ID.
     *
     * @param id expense ID
     * @return mapped {@link ExpenseResponse}
     * @throws ResourceNotFoundException if expense with given ID is not found
     */
    @Override
    public ExpenseResponse findExpenseById(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense with id " + id + " not found"));
        ExpenseResponse response = expenseMapper.mapTo(expense, ExpenseResponse.class);
        response.setResource(resourceMapper.mapTo(expense.getResource(), ResourceResponse.class));
        return response;
    }

    /**
     * Creates a new expense.
     * Looks up the resource by ID, throws if resource not found.
     *
     * @param newExpenseRequest DTO containing expense creation data
     * @return mapped {@link ExpenseResponse} of saved entity
     * @throws ResourceNotFoundException if resource with given ID does not exist
     */
    @Override
    public ExpenseResponse createExpense(NewExpenseRequest newExpenseRequest) {
        Expense expense = new Expense();
        expense.setName(newExpenseRequest.getName());
        expense.setDescription(newExpenseRequest.getDescription());

        Resource resource = resourceRepository.findById(newExpenseRequest.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource with id " + newExpenseRequest.getResourceId() + " not found"));
        expense.setResource(resource);

        expense.setQuantity(newExpenseRequest.getQuantity());
        expense.setCost(newExpenseRequest.getCost());

        Expense saved = expenseRepository.save(expense);

        ExpenseResponse response = expenseMapper.mapTo(saved, ExpenseResponse.class);
        response.setResource(resourceMapper.mapTo(saved.getResource(), ResourceResponse.class));
        return response;
    }

    /**
     * Partially updates an existing expense.
     * Only non-null fields in {@code updateExpenseRequest} are applied.
     *
     * @param id ID of the expense to update
     * @param updateExpenseRequest DTO with fields to update
     * @return updated and mapped {@link ExpenseResponse}
     * @throws ResourceNotFoundException if expense or resource (when changing resource) is not found
     */
    @Override
    public ExpenseResponse partialUpdateExpense(Long id, UpdateExpenseRequest updateExpenseRequest) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense with id " + id + " not found"));

        Optional.ofNullable(updateExpenseRequest.getName()).ifPresent(expense::setName);
        Optional.ofNullable(updateExpenseRequest.getDescription()).ifPresent(expense::setDescription);

        if (updateExpenseRequest.getResourceId() != null) {
            Resource resource = resourceRepository.findById(updateExpenseRequest.getResourceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Resource with id " + updateExpenseRequest.getResourceId() + " not found"));
            expense.setResource(resource);
        }

        Optional.ofNullable(updateExpenseRequest.getQuantity()).ifPresent(expense::setQuantity);
        Optional.ofNullable(updateExpenseRequest.getCost()).ifPresent(expense::setCost);

        Expense updated = expenseRepository.save(expense);
        ExpenseResponse response = expenseMapper.mapTo(updated, ExpenseResponse.class);
        response.setResource(resourceMapper.mapTo(updated.getResource(), ResourceResponse.class));
        return response;
    }

    /**
     * Deletes an expense by its ID.
     *
     * @param id ID of the expense to delete
     * @throws ResourceNotFoundException if expense does not exist
     */
    @Override
    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Expense with id " + id + " not found");
        }
        expenseRepository.deleteById(id);
    }

    /**
     * Gets the summary of incomes and expenses for a given date range and resource filter.
     *
     * @param from start datetime (inclusive)
     * @param to end datetime (inclusive)
     * @param resourceName name of the resource to filter by, or "all" for all resources
     * @return summary containing sums of incomes and expenses
     */
    @Override
    public ExpenseSummaryResponse getExpenseSummary(LocalDateTime from, LocalDateTime to, String resourceName) {
        BigDecimal incomeSum;
        BigDecimal expenseSum;
        if ("all".equalsIgnoreCase(resourceName)) {
            incomeSum = expenseRepository.sumIncomes(from, to);
            expenseSum = expenseRepository.sumExpenses(from, to);
        } else {
            incomeSum = expenseRepository.sumIncomesByResourceName(from, to, resourceName);
            expenseSum = expenseRepository.sumExpensesByResourceName(from, to, resourceName);
        }
        return ExpenseSummaryResponse.builder()
                .incomeSum(incomeSum)
                .expenseSum(expenseSum)
                .build();
    }

    /**
     * Finds expenses filtered by date range, type, and resource.
     * Supports filtering by incomes (cost > 0), expenses (cost < 0), or all types.
     *
     * @param from start datetime (inclusive)
     * @param to end datetime (inclusive)
     * @param type filter type: "incomes", "expenses", or other for all
     * @param resourceName resource name to filter by or "all"
     * @param pageable pagination and sorting information
     * @return paginated list of mapped {@link ExpenseResponse}
     */
    @Override
    public Page<ExpenseResponse> findExpensesByDateRange(LocalDateTime from, LocalDateTime to, String type, String resourceName, Pageable pageable) {
        Page<Expense> expenses;
        if (!"all".equalsIgnoreCase(resourceName)) {
            if ("incomes".equalsIgnoreCase(type)) {
                expenses = expenseRepository.findAllByCreatedAtBetweenAndResourceNameAndCostGreaterThan(from, to, resourceName, BigDecimal.ZERO, pageable);
            } else if ("expenses".equalsIgnoreCase(type)) {
                expenses = expenseRepository.findAllByCreatedAtBetweenAndResourceNameAndCostLessThan(from, to, resourceName, BigDecimal.ZERO, pageable);
            } else {
                expenses = expenseRepository.findAllByCreatedAtBetweenAndResourceName(from, to, resourceName, pageable);
            }
        } else {
            if ("incomes".equalsIgnoreCase(type)) {
                expenses = expenseRepository.findAllByCreatedAtBetweenAndCostGreaterThan(from, to, BigDecimal.ZERO, pageable);
            } else if ("expenses".equalsIgnoreCase(type)) {
                expenses = expenseRepository.findAllByCreatedAtBetweenAndCostLessThan(from, to, BigDecimal.ZERO, pageable);
            } else {
                expenses = expenseRepository.findAllByCreatedAtBetween(from, to, pageable);
            }
        }
        return expenses.map(expense -> {
            ExpenseResponse response = expenseMapper.mapTo(expense, ExpenseResponse.class);
            response.setResource(resourceMapper.mapTo(expense.getResource(), ResourceResponse.class));
            return response;
        });
    }

}
