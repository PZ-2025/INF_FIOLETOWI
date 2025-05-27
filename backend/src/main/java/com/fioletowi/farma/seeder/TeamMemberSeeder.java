package com.fioletowi.farma.seeder;

import com.fioletowi.farma.team.Team;
import com.fioletowi.farma.team.TeamMember;
import com.fioletowi.farma.team.TeamMemberRepository;
import com.fioletowi.farma.team.TeamRepository;
import com.fioletowi.farma.user.User;
import com.fioletowi.farma.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Data seeder for TeamMember entities.
 *
 * <p>This component seeds initial team members by linking existing users with an existing team.
 * It only runs if there are no existing team members and when the application is not running under the "test" profile.</p>
 *
 * <p>The seeder assigns users with IDs 4 and 5 to the team with ID 1.</p>
 *
 * <p>Runs with order 8 to ensure prior seeders (like users and teams) have already populated the database.</p>
 */
@Component
@AllArgsConstructor
@Order(8)
@Profile("!test")
public class TeamMemberSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if team members already exist; if so, skip seeding
        if (teamMemberRepository.count() != 0) {
            return;
        }

        // Retrieve users with IDs 4 and 5
        User user4 = userRepository.findById(4L).orElseThrow(() -> new RuntimeException("User with ID 4 not found"));
        User user5 = userRepository.findById(5L).orElseThrow(() -> new RuntimeException("User with ID 5 not found"));

        // Retrieve team with ID 1
        Team team = teamRepository.findById(1L).orElseThrow(() -> new RuntimeException("Team with ID 1 not found"));

        // Create new team members
        TeamMember teamMember4 = new TeamMember();
        teamMember4.setTeam(team);
        teamMember4.setUser(user4);

        TeamMember teamMember5 = new TeamMember();
        teamMember5.setTeam(team);
        teamMember5.setUser(user5);

        // Save new team members
        teamMemberRepository.save(teamMember4);
        teamMemberRepository.save(teamMember5);

        System.out.println("Seeded members to team: " + team.getName());
    }
}
