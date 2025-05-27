package com.fioletowi.farma.seeder;

import com.fioletowi.farma.team.Team;
import com.fioletowi.farma.team.TeamRepository;
import com.fioletowi.farma.user.User;
import com.fioletowi.farma.user.UserRepository;
import com.fioletowi.farma.user.UserRole;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Data seeder for Team entities.
 *
 * <p>This seeder creates an initial team called "Team Alpha" and assigns a leader from the existing users
 * with the role MANAGER. It only runs if no teams exist yet and skips if no manager is found.</p>
 *
 * <p>Runs with order 4 to ensure prerequisite user data is seeded beforehand.</p>
 *
 * <p>Active only when the "test" profile is not active.</p>
 */
@Component
@AllArgsConstructor
@Order(4)
@Profile("!test")
public class TeamSeeder implements CommandLineRunner {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        if (teamRepository.count() != 0) {
            return;
        }

        // Pobieramy listę managerów – zakładamy, że mamy metodę w userRepository (w razie potrzeby filtrujemy manualnie)
        Page<User> managers = userRepository.findAllByUserRole(PageRequest.of(0, 1000),UserRole.MANAGER);
        User leader = null;
        for (User u : managers) {
            if (u.getUserRole().equals(UserRole.MANAGER)) {
                leader = u;
                break;
            }
        }
        if (leader == null) {
            System.out.println("Brak managera. Nie można seedować zespołu.");
            return;
        }

        Team team = new Team();
        team.setName("Team Alpha");
        team.setLeader(leader);
        teamRepository.save(team);
        System.out.println("Seeded team: " + team.getName());
    }
}
