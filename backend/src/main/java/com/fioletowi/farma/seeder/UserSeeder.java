package com.fioletowi.farma.seeder;

import com.fioletowi.farma.user.User;
import com.fioletowi.farma.user.UserRepository;
import com.fioletowi.farma.user.UserRole;
import com.fioletowi.farma.user.UserStatus;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Seeds initial User data into the database.
 *
 * <p>This seeder creates five users with different roles:
 * - One OWNER
 * - Two MANAGERs
 * - Two WORKERs
 *
 * All users have preset attributes including encrypted passwords,
 * active status, hire date set to the current time, and notification settings enabled.</p>
 *
 * <p>The seeder runs only if no users currently exist in the database.</p>
 *
 * <p>Execution order is 1 to ensure users exist before other related seeders.</p>
 *
 * <p>Active only when the "test" profile is not enabled.</p>
 */
@Component
@AllArgsConstructor
@Order(1)
@Profile("!test")
public class UserSeeder implements CommandLineRunner {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if(userRepository.count() != 0)
            return;

        User user1 = User.builder()
                .firstName("Owner")
                .lastName("Owner")
                .email("owner@example.com")
                .password(passwordEncoder.encode("password"))
                .birthDate(LocalDateTime.of(2000, 1, 1, 0, 0))
                .hiredAt(LocalDateTime.now())
                .terminatedAt(null)
                .status(UserStatus.ACTIVE)
                .note("Owner")
                .efficiency(0.0)
                .address("owner address")
                .phoneNumber("123456789")
                .userRole(UserRole.OWNER)
                .allowNotifications(true)
                .isArchived(false)
                .build();

        User user2 = User.builder()
                .firstName("Manager")
                .lastName("1")
                .email("manager1@example.com")
                .password(passwordEncoder.encode("password"))
                .birthDate(LocalDateTime.of(2001, 1, 1, 0, 0))
                .hiredAt(LocalDateTime.now())
                .terminatedAt(null)
                .status(UserStatus.ACTIVE)
                .note("Manager1")
                .efficiency(0.0)
                .address("manager1 address")
                .phoneNumber("987654321")
                .userRole(UserRole.MANAGER)
                .allowNotifications(true)
                .isArchived(false)
                .build();

        User user3 = User.builder()
                .firstName("Manager")
                .lastName("2")
                .email("manager2@example.com")
                .password(passwordEncoder.encode("password"))
                .birthDate(LocalDateTime.of(2002, 1, 1, 0, 0))
                .hiredAt(LocalDateTime.now())
                .terminatedAt(null)
                .status(UserStatus.ACTIVE)
                .note("Manager2")
                .efficiency(0.0)
                .address("manager2 address")
                .phoneNumber("998877665")
                .userRole(UserRole.MANAGER)
                .allowNotifications(true)
                .isArchived(false)
                .build();

        User user4 = User.builder()
                .firstName("Worker")
                .lastName("1")
                .email("worker1@example.com")
                .password(passwordEncoder.encode("password"))
                .birthDate(LocalDateTime.of(2004, 1, 1, 0, 0))
                .hiredAt(LocalDateTime.now())
                .terminatedAt(null)
                .status(UserStatus.ACTIVE)
                .note("Worker1")
                .efficiency(0.0)
                .address("worker1 address")
                .phoneNumber("112233445")
                .userRole(UserRole.WORKER)
                .allowNotifications(true)
                .isArchived(false)
                .build();

        User user5 = User.builder()
                .firstName("Worker")
                .lastName("2")
                .email("worker2@example.com")
                .password(passwordEncoder.encode("password"))
                .birthDate(LocalDateTime.of(2005, 1, 1, 0, 0))
                .hiredAt(LocalDateTime.now())
                .terminatedAt(null)
                .status(UserStatus.ACTIVE)
                .note("Worker2")
                .efficiency(0.0)
                .address("worker2 address")
                .phoneNumber("113355779")
                .userRole(UserRole.WORKER)
                .allowNotifications(true)
                .isArchived(false)
                .build();


        userRepository.saveAll(List.of(user1, user2, user3, user4, user5));
        System.out.println("Seeded " + userRepository.count() + " users");
    }
}
