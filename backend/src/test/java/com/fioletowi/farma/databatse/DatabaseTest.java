package com.fioletowi.farma.databatse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:testdb")
public class DatabaseTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void testConnectionClosed() throws Exception {
        // Zamykamy połączenie z bazą
        dataSource.getConnection().close();

        // Próba wykonania operacji po zamknięciu połączenia powinna rzucić wyjątek
        assertThrows(DataAccessException.class, () -> {
            // Tutaj próbujemy wykonać operację na bazie danych, np. zapisać rekord
            // Możesz użyć repository, JdbcTemplate lub innego mechanizmu, który wykorzystuje połączenie

            // Poniżej przykładowa operacja (w zależności od tego, co używasz):
            // repository.save(new SomeEntity());
            // Lub prosta próba wykonania zapytania za pomocą JdbcTemplate
            // jdbcTemplate.update("INSERT INTO some_table (col1, col2) VALUES (?, ?)", "value1", "value2");

            // Ponieważ połączenie zostało zamknięte, spodziewamy się wyjątku DataAccessException.
        });
    }

    @Test
    void testTransactionAfterConnectionClosed() throws Exception {
        dataSource.getConnection().close();
        assertThrows(DataAccessException.class, () -> {
            TransactionStatus status = transactionManager.getTransaction(null);
            transactionManager.commit(status);
        });
    }
}
