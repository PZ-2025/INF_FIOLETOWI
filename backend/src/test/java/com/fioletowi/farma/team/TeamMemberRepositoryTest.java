package com.fioletowi.farma.team;

import com.fioletowi.farma.user.User;
import com.fioletowi.farma.team.Team;
import com.fioletowi.farma.user.UserRepository;
import com.fioletowi.farma.user.UserRole;
import lombok.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class TeamMemberRepositoryTest {

    @Autowired TeamMemberRepository teamMemberRepository;
    @Autowired TeamRepository teamRepository;
    @Autowired UserRepository userRepository;

    private Team teamA, teamB;
    private User user1, user2;
    private TeamMember tm1, tm2;

    @BeforeEach
    void setUp() {
        teamMemberRepository.deleteAll();
        teamRepository.deleteAll();
        userRepository.deleteAll();

        // Tworzymy teamy
        teamA = new Team();
        teamA.setName("Team A");
        teamA = teamRepository.save(teamA);

        teamB = new Team();
        teamB.setName("Team B");
        teamB = teamRepository.save(teamB);

        // Tworzymy użytkowników
        user1 = new User();
        user1.setFirstName("U1");
        user1.setLastName("Test");
        user1.setEmail("u1@t.test");
        user1.setPassword("p");
        user1.setBirthDate(LocalDateTime.now());
        user1.setHiredAt(LocalDateTime.now());
        user1.setUserRole(UserRole.WORKER);
        user1.setAddress("X");
        user1.setPhoneNumber("1");
        user1.setAllowNotifications(true);
        user1.setIsArchived(false);
        user1.setEfficiency(1.0);
        user1 = userRepository.save(user1);

        user2 = new User();
        user2.setFirstName("U2");
        user2.setLastName("Test");
        user2.setEmail("u2@t.test");
        user2.setPassword("p");
        user2.setBirthDate(LocalDateTime.now());
        user2.setHiredAt(LocalDateTime.now());
        user2.setUserRole(UserRole.WORKER);
        user2.setAddress("Y");
        user2.setPhoneNumber("2");
        user2.setAllowNotifications(false);
        user2.setIsArchived(false);
        user2.setEfficiency(0.5);
        user2 = userRepository.save(user2);

        // Tworzymy TeamMember
        tm1 = new TeamMember();
        tm1.setTeam(teamA);
        tm1.setUser(user1);
        tm1.setCreatedAt(LocalDateTime.now());
        tm1 = teamMemberRepository.save(tm1);

        tm2 = new TeamMember();
        tm2.setTeam(teamA);
        tm2.setUser(user2);
        tm2.setCreatedAt(LocalDateTime.now());
        tm2 = teamMemberRepository.save(tm2);
    }

    @Test
    void testFindAll() {
        var page = teamMemberRepository.findAll(PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent())
                .extracting(tm -> tm.getUser().getEmail())
                .containsExactlyInAnyOrder("u1@t.test", "u2@t.test");
    }

    @Test
    void testFindUsersByTeamId() {
        var users = teamMemberRepository.findUsersByTeamId(teamA.getId());
        assertThat(users).hasSize(2)
                .extracting(User::getId)
                .containsExactlyInAnyOrder(user1.getId(), user2.getId());
    }

    @Test
    void testCountByTeamId() {
        long cnt = teamMemberRepository.countByTeamId(teamA.getId());
        assertThat(cnt).isEqualTo(2);
    }

    @Test
    void testFindByUserAndTeam() {
        var maybe = teamMemberRepository.findByUserIdAndTeamId(user1.getId(), teamA.getId());
        assertThat(maybe).isPresent();
        assertThat(maybe.get().getId()).isEqualTo(tm1.getId());
    }

    @Test
    void testDelete() {
        teamMemberRepository.delete(tm1);
        assertThat(teamMemberRepository.existsById(tm1.getId())).isFalse();
    }
}