package com.fioletowi.farma.task;

import com.fioletowi.farma.team.Team;
import com.fioletowi.farma.user.User;
import com.fioletowi.farma.user.UserRole;
import com.fioletowi.farma.team.TeamMember;
import com.fioletowi.farma.team.TeamRepository;
import com.fioletowi.farma.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTaskRepository userTaskRepository;

    private Team teamA, teamB;
    private Task t1, t2;
    private User user;

    @BeforeEach
    void setUp() {
        // czyścimy tabele
        userTaskRepository.deleteAll();
        taskRepository.deleteAll();
        teamRepository.deleteAll();
        userRepository.deleteAll();

        // seed Team
        teamA = new Team();
        teamA.setName("TeamA");
        teamRepository.save(teamA);

        teamB = new Team();
        teamB.setName("TeamB");
        teamRepository.save(teamB);

        // seed Tasks
        t1 = new Task();
        t1.setName("Task1");
        t1.setTaskProgress(TaskProgress.NOT_STARTED);
        t1.setTeam(teamA);
        t1.setStartDate(LocalDateTime.now().minusDays(1));
        t1.setEndDate(LocalDateTime.now().plusDays(1));
        taskRepository.save(t1);

        t2 = new Task();
        t2.setName("Task2");
        t2.setTaskProgress(TaskProgress.COMPLETED);
        t2.setTeam(teamB);
        t2.setStartDate(LocalDateTime.now().minusDays(2));
        t2.setEndDate(LocalDateTime.now().minusDays(1));
        taskRepository.save(t2);

        // seed User + UserTask for countByUserAndStatus
        user = User.builder()
                .firstName("U").lastName("Ser")
                .email("u@test")
                .password("x")
                .birthDate(LocalDateTime.now().minusYears(20))
                .hiredAt(LocalDateTime.now())
                .userRole(UserRole.WORKER)
                .address("A").phoneNumber("0")
                .allowNotifications(true).isArchived(false).efficiency(1.0)
                .build();
        userRepository.save(user);

        UserTask ut = new UserTask();
        ut.setTask(t1);
        ut.setUser(user);
        userTaskRepository.save(ut);
    }

    @Test
    @DisplayName("findAll(Pageable) returns paginated tasks")
    void testFindAllPageable() {
        Page<Task> page = taskRepository.findAll(PageRequest.of(0, 1));
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("findByIsArchivedFalse returns only non-archived")
    void testFindByIsArchivedFalse() {
        t1.setArchived(true);
        taskRepository.save(t1);
        List<Task> list = taskRepository.findByIsArchivedFalse();
        assertThat(list).containsExactly(t2);
    }

    @Test
    @DisplayName("countByIsArchivedFalseAndTaskProgressIn")
    void testCountByIsArchivedFalseAndProgress() {
        long cnt = taskRepository.countByIsArchivedFalseAndTaskProgressIn(
                List.of(TaskProgress.NOT_STARTED, TaskProgress.COMPLETED));
        assertThat(cnt).isEqualTo(2);
    }

    @Test
    @DisplayName("countByUserAndStatus")
    void testCountByUserAndStatus() {
        long cnt = taskRepository.countByUserAndStatus(
                user.getId(), TaskProgress.NOT_STARTED,
                LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2)
        );
        assertThat(cnt).isEqualTo(1);
    }

    @Test
    @DisplayName("countByTeamAndStatus")
    void testCountByTeamAndStatus() {
        long cnt = taskRepository.countByTeamAndStatus(
                teamA.getId(), TaskProgress.NOT_STARTED,
                LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2)
        );
        assertThat(cnt).isEqualTo(1);
    }

    @Test
    @DisplayName("findAllByTeamId returns correct tasks")
    void testFindAllByTeamId() {
        List<Task> list = taskRepository.findAllByTeamId(teamB.getId());
        assertThat(list).containsExactly(t2);
    }

    @Test
    @DisplayName("findUnassignedTasksByLeaderId excludes assigned")
    void testFindUnassignedTasksByLeaderId() {
        // ustawienie leadera w teamA i przypisanie do t1
        User leader = userRepository.save(user);
        teamA.setLeader(leader);
        teamRepository.save(teamA);

        List<Task> unassigned = taskRepository.findUnassignedTasksByLeaderId(leader.getId());
        assertThat(unassigned).isEmpty(); // bo t1 ma userTask
    }

    @Test
    @DisplayName("countByTeamLeaderIdAndIsArchivedFalse")
    void testCountByTeamLeaderIdAndIsArchivedFalse() {
        User leader = user;
        teamA.setLeader(leader);
        teamRepository.save(teamA);

        long cnt = taskRepository.countByTeamLeaderIdAndIsArchivedFalse(leader.getId());
        assertThat(cnt).isEqualTo(1);
    }
}