package com.fioletowi.farma.resource;

import com.fioletowi.farma.exception.ResourceNotFoundException;
import com.fioletowi.farma.mapper.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
// Wyłączamy „strict stubbing”, żeby niezadeklarowane wywołania stubów nie kończyły testu błędem
@MockitoSettings(strictness = Strictness.LENIENT)
class ExpenseServiceImplTest {

    @Mock ExpenseRepository expenseRepository;
    @Mock ResourceRepository resourceRepository;
    @Mock Mapper<Expense, ExpenseResponse> expenseMapper;
    @Mock Mapper<Resource, ResourceResponse> resourceMapper;

    @InjectMocks ExpenseServiceImpl expenseService;

    Expense sampleExpense;
    ExpenseResponse sampleExpenseResp;
    Resource sampleResource;
    ResourceResponse sampleResourceResp;
    NewExpenseRequest newReq;
    UpdateExpenseRequest updReq;

    @BeforeEach
    void setUp() {
        // Przygotowujemy sampleResource i sampleResourceResp
        sampleResource = new Resource();
        sampleResource.setId(1L);
        sampleResource.setName("R");
        sampleResource.setQuantity(BigDecimal.valueOf(5));
        sampleResource.setResourceType(ResourceType.FOR_USE);

        sampleResourceResp = new ResourceResponse();
        sampleResourceResp.setId(1L);
        sampleResourceResp.setName("R");
        sampleResourceResp.setQuantity(BigDecimal.valueOf(5));
        sampleResourceResp.setResourceType(ResourceType.FOR_USE);

        // Przygotowujemy sampleExpense i sampleExpenseResp
        sampleExpense = new Expense();
        sampleExpense.setId(10L);
        sampleExpense.setName("E");
        sampleExpense.setDescription("D");
        sampleExpense.setResource(sampleResource);
        sampleExpense.setQuantity(BigDecimal.valueOf(2));
        sampleExpense.setCost(BigDecimal.valueOf(100));
        sampleExpense.setCreatedAt(LocalDateTime.now());

        sampleExpenseResp = new ExpenseResponse();
        sampleExpenseResp.setId(10L);
        sampleExpenseResp.setName("E");
        sampleExpenseResp.setDescription("D");
        sampleExpenseResp.setQuantity(BigDecimal.valueOf(2));
        sampleExpenseResp.setCost(BigDecimal.valueOf(100));
        sampleExpenseResp.setCreatedAt(sampleExpense.getCreatedAt());
        sampleExpenseResp.setResource(sampleResourceResp);

        // Requesty
        newReq = NewExpenseRequest.builder()
                .name("E")
                .description("D")
                .resourceId(1L)
                .quantity(BigDecimal.valueOf(2))
                .cost(BigDecimal.valueOf(100))
                .build();

        updReq = UpdateExpenseRequest.builder()
                .name("E2")
                .description("D2")
                .resourceId(1L)
                .quantity(BigDecimal.valueOf(3))
                .cost(BigDecimal.valueOf(150))
                .build();

        // Lenient stubbing: expenseMapper.mapTo zawsze zwraca sampleExpenseResp
        lenient().when(expenseMapper.mapTo(any(Expense.class), eq(ExpenseResponse.class)))
                .thenReturn(sampleExpenseResp);
        // Lenient stubbing: resourceMapper.mapTo(Resource->ResourceResponse)
        lenient().when(resourceMapper.mapTo(any(Resource.class), eq(ResourceResponse.class)))
                .thenReturn(sampleResourceResp);
    }

    @Test
    void findExpenseById_notFound() {
        given(expenseRepository.findById(5L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> expenseService.findExpenseById(5L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Expense with id 5 not found");
    }


    @Test
    void createExpense_resourceNotFound() {
        given(resourceRepository.findById(1L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> expenseService.createExpense(newReq))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Resource with id 1 not found");
    }

    @Test
    void partialUpdateExpense() {
        // 1) istniejący Expense
        given(expenseRepository.findById(10L)).willReturn(Optional.of(sampleExpense));
        // 2) istniejący Resource (bo updReq.getResourceId()!=null)
        given(resourceRepository.findById(1L)).willReturn(Optional.of(sampleResource));
        // 3) po save zwróć sampleExpense
        given(expenseRepository.save(sampleExpense)).willReturn(sampleExpense);
        // 4) expenseMapper i resourceMapper muszą zwracać nie-null
        given(expenseMapper.mapTo(sampleExpense, ExpenseResponse.class))
                .willReturn(sampleExpenseResp);
        given(resourceMapper.mapTo(sampleResource, ResourceResponse.class))
                .willReturn(sampleResourceResp);
    }


    @Test
    void deleteExpense_notFound() {
        given(expenseRepository.existsById(5L)).willReturn(false);
        assertThatThrownBy(() -> expenseService.deleteExpense(5L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteExpense_found() {
        given(expenseRepository.existsById(10L)).willReturn(true);
        willDoNothing().given(expenseRepository).deleteById(10L);

        expenseService.deleteExpense(10L);
        then(expenseRepository).should().deleteById(10L);
    }

    @Test
    void getExpenseSummary_all() {
        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now();
        given(expenseRepository.sumIncomes(from, to)).willReturn(BigDecimal.valueOf(300));
        given(expenseRepository.sumExpenses(from, to)).willReturn(BigDecimal.valueOf(-100));

        ExpenseSummaryResponse sum = expenseService.getExpenseSummary(from, to, "all");
        assertThat(sum.getIncomeSum()).isEqualByComparingTo("300");
        assertThat(sum.getExpenseSum()).isEqualByComparingTo("-100");
    }

    @Test
    void getExpenseSummary_byResource() {
        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now();
        given(expenseRepository.sumIncomesByResourceName(from, to, "R")).willReturn(BigDecimal.valueOf(200));
        given(expenseRepository.sumExpensesByResourceName(from, to, "R")).willReturn(BigDecimal.valueOf(-50));

        ExpenseSummaryResponse sum = expenseService.getExpenseSummary(from, to, "R");
        assertThat(sum.getIncomeSum()).isEqualByComparingTo("200");
        assertThat(sum.getExpenseSum()).isEqualByComparingTo("-50");
    }

    @Test
    void findExpensesByDateRange_byResource_expenses() {
        LocalDateTime from = LocalDateTime.now().minusDays(1), to = LocalDateTime.now();
        Page<Expense> page = new PageImpl<>(List.of(sampleExpense));
        given(expenseRepository.findAllByCreatedAtBetweenAndResourceNameAndCostLessThan(from, to, "R", BigDecimal.ZERO, PageRequest.of(0,5)))
                .willReturn(page);

        Page<ExpenseResponse> out = expenseService.findExpensesByDateRange(from, to, "expenses", "R", PageRequest.of(0,5));
        assertThat(out.getTotalElements()).isEqualTo(1);
    }
}
