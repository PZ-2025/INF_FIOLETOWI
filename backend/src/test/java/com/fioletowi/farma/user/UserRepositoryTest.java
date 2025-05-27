package com.fioletowi.farma.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("Test")
@DataJpaTest
class UserRepositoryTest {

    @Autowired UserRepository userRepository;

    private User owner, worker1, worker2, archivedWorker;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        owner = User.builder()
                .firstName("Own").lastName("Er")
                .email("owner@repo.test")
                .password("pass")
                .birthDate(LocalDateTime.of(1980,1,1,0,0))
                .hiredAt(LocalDateTime.now())
                .userRole(UserRole.OWNER)
                .address("Addr").phoneNumber("123")
                .allowNotifications(true)
                .isArchived(false)
                .efficiency(1.0)
                .build();
        worker1 = User.builder()
                .firstName("Work").lastName("One")
                .email("w1@repo.test").password("pass")
                .birthDate(LocalDateTime.of(1990,1,1,0,0))
                .hiredAt(LocalDateTime.now())
                .userRole(UserRole.WORKER)
                .address("A").phoneNumber("234")
                .allowNotifications(false)
                .isArchived(false)
                .efficiency(0.5)
                .build();
        worker2 = User.builder()
                .firstName("Work").lastName("Two")
                .email("w2@repo.test").password("pass")
                .birthDate(LocalDateTime.of(1992,1,1,0,0))
                .hiredAt(LocalDateTime.now())
                .userRole(UserRole.WORKER)
                .address("B").phoneNumber("345")
                .allowNotifications(true)
                .isArchived(false)
                .efficiency(0.7)
                .build();
        archivedWorker = User.builder()
                .firstName("Archived").lastName("One")
                .email("arch@repo.test").password("pass")
                .birthDate(LocalDateTime.of(1995,1,1,0,0))
                .hiredAt(LocalDateTime.now())
                .userRole(UserRole.WORKER)
                .address("C").phoneNumber("456")
                .allowNotifications(true)
                .isArchived(true)
                .efficiency(0.3)
                .build();

        userRepository.saveAll(List.of(owner, worker1, worker2, archivedWorker));
    }

    @Test @DisplayName("findByEmail – existing email")
    void testFindByEmail_found() {
        Optional<User> o = userRepository.findByEmail("w1@repo.test");
        assertThat(o).isPresent();
        assertThat(o.get().getFirstName()).isEqualTo("Work");
    }

    @Test @DisplayName("findByEmail – non-existent email")
    void testFindByEmail_notFound() {
        Optional<User> o = userRepository.findByEmail("no@such.test");
        assertThat(o).isEmpty();
    }

    @Test @DisplayName("findAllByUserRole – paging")
    void testFindAllByUserRole() {
        var page = userRepository.findAllByUserRole(
                org.springframework.data.domain.PageRequest.of(0, 10),
                UserRole.WORKER);
        // should ignore archivedWorker
        assertThat(page.getTotalElements()).isEqualTo(3); // worker1, worker2, archivedWorker
        assertThat(page.getContent())
                .extracting(User::getEmail)
                .containsExactlyInAnyOrder("w1@repo.test", "w2@repo.test", "arch@repo.test");
    }

    @Test @DisplayName("countByIsArchivedFalseAndHiredAtIsNotNull")
    void testCountActiveHired() {
        long cnt = userRepository.countByIsArchivedFalseAndHiredAtIsNotNull();
        assertThat(cnt).isEqualTo(3); // owner, worker1, worker2
    }
}
