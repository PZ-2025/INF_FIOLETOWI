package com.fioletowi.farma.resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class ExpenseRepositoryTest {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    private Resource resA;
    private Resource resB;

    @BeforeEach
    void setUp() {
        // czyścimy obie tabele
        expenseRepository.deleteAll();
        resourceRepository.deleteAll();

        // tworzymy dwa zasoby
        resA = new Resource();
        resA.setName("ResA");
        resA.setQuantity(BigDecimal.valueOf(1));
        resA.setResourceType(ResourceType.FOR_USE);
        resourceRepository.save(resA);

        resB = new Resource();
        resB.setName("ResB");
        resB.setQuantity(BigDecimal.valueOf(1));
        resB.setResourceType(ResourceType.FOR_USE);
        resourceRepository.save(resB);

        // seedujemy wydatki/przychody:
        // -2 przychody (+100 i +200) wczoraj i dziś na ResA
        // -2 wydatki (-50 i -75) wczoraj i dziś na ResB
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);

        Expense e1 = new Expense();
        e1.setName("Income1");
        e1.setResource(resA);
        e1.setQuantity(BigDecimal.valueOf(1));
        e1.setCost(BigDecimal.valueOf(100));
        e1.setCreatedAt(yesterday);
        expenseRepository.save(e1);

        Expense e2 = new Expense();
        e2.setName("Income2");
        e2.setResource(resA);
        e2.setQuantity(BigDecimal.valueOf(1));
        e2.setCost(BigDecimal.valueOf(200));
        e2.setCreatedAt(now);
        expenseRepository.save(e2);

        Expense e3 = new Expense();
        e3.setName("Expense1");
        e3.setResource(resB);
        e3.setQuantity(BigDecimal.valueOf(1));
        e3.setCost(BigDecimal.valueOf(-50));
        e3.setCreatedAt(yesterday);
        expenseRepository.save(e3);

        Expense e4 = new Expense();
        e4.setName("Expense2");
        e4.setResource(resB);
        e4.setQuantity(BigDecimal.valueOf(1));
        e4.setCost(BigDecimal.valueOf(-75));
        e4.setCreatedAt(now);
        expenseRepository.save(e4);
    }

    @Test
    @DisplayName("sumIncomes() liczy tylko koszty > 0 w zakresie dat")
    void testSumIncomes() {
        LocalDateTime from = LocalDateTime.now().minusDays(2);
        LocalDateTime to = LocalDateTime.now().plusDays(1);

        BigDecimal totalIncome = expenseRepository.sumIncomes(from, to);
        // 100 + 200 = 300
        assertThat(totalIncome).isEqualByComparingTo("300");
    }

    @Test
    @DisplayName("sumExpenses() liczy tylko koszty < 0 w zakresie dat")
    void testSumExpenses() {
        LocalDateTime from = LocalDateTime.now().minusDays(2);
        LocalDateTime to = LocalDateTime.now().plusDays(1);

        BigDecimal totalExpense = expenseRepository.sumExpenses(from, to);
        // -50 + -75 = -125
        assertThat(totalExpense).isEqualByComparingTo("-125");
    }

    @Test
    @DisplayName("findAllByCreatedAtBetween zwraca stronę wszystkich rekordów w zakresie")
    void testFindAllByCreatedAtBetween() {
        LocalDateTime from = LocalDateTime.now().minusDays(2);
        LocalDateTime to = LocalDateTime.now().plusDays(1);
        Page<Expense> page = expenseRepository.findAllByCreatedAtBetween(from, to, PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isEqualTo(4);
    }

    @Test
    @DisplayName("findAllByCreatedAtBetweenAndCostGreaterThan zwraca tylko przychody")
    void testFindAllByCreatedAtBetweenAndCostGreaterThan() {
        LocalDateTime from = LocalDateTime.now().minusDays(2);
        LocalDateTime to = LocalDateTime.now().plusDays(1);
        // cost > 0
        Page<Expense> page = expenseRepository
                .findAllByCreatedAtBetweenAndCostGreaterThan(from, to, BigDecimal.ZERO, PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isEqualTo(2);
        page.forEach(e -> assertThat(e.getCost()).isGreaterThan(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("findAllByCreatedAtBetweenAndCostLessThan zwraca tylko wydatki")
    void testFindAllByCreatedAtBetweenAndCostLessThan() {
        LocalDateTime from = LocalDateTime.now().minusDays(2);
        LocalDateTime to = LocalDateTime.now().plusDays(1);
        // cost < 0
        Page<Expense> page = expenseRepository
                .findAllByCreatedAtBetweenAndCostLessThan(from, to, BigDecimal.ZERO, PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isEqualTo(2);
        page.forEach(e -> assertThat(e.getCost()).isLessThan(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("sumIncomesByResourceName i sumExpensesByResourceName działają per resource")
    void testSumByResourceName() {
        LocalDateTime from = LocalDateTime.now().minusDays(2);
        LocalDateTime to = LocalDateTime.now().plusDays(1);

        BigDecimal incA = expenseRepository.sumIncomesByResourceName(from, to, "ResA");
        BigDecimal expA = expenseRepository.sumExpensesByResourceName(from, to, "ResA");
        // ResA ma tylko przychody: 100 + 200 = 300
        assertThat(incA).isEqualByComparingTo("300");
        assertThat(expA).isEqualByComparingTo("0");

        BigDecimal incB = expenseRepository.sumIncomesByResourceName(from, to, "ResB");
        BigDecimal expB = expenseRepository.sumExpensesByResourceName(from, to, "ResB");
        // ResB ma tylko wydatki: -50 + -75 = -125
        assertThat(incB).isEqualByComparingTo("0");
        assertThat(expB).isEqualByComparingTo("-125");
    }
}
