package com.fioletowi.farma.task;

import com.fioletowi.farma.team.Team;
import com.fioletowi.farma.team.TeamMember;
import com.fioletowi.farma.team.TeamRepository;
import com.fioletowi.farma.user.User;
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
class UserTaskRepositoryTest {

    @Autowired UserTaskRepository userTaskRepository;
    @Autowired TaskRepository taskRepository;
    @Autowired UserRepository userRepository;
    @Autowired TeamRepository teamRepository;

    private Task taskA;
    private User userA, userB;
    private Team team;

    @BeforeEach
    void setUp() {
        // clear
        userTaskRepository.deleteAll();
        taskRepository.deleteAll();
        userRepository.deleteAll();
        teamRepository.deleteAll();

        // seed team
        team = new Team();
        team.setName("T1");
        teamRepository.save(team);

        // seed users
        userA = User.builder()
                .firstName("UA").lastName("A")
                .email("ua@t.test").password("x")
                .birthDate(LocalDateTime.now().minusYears(20))
                .hiredAt(LocalDateTime.now())
                .userRole(null).address("").phoneNumber("")
                .allowNotifications(true).isArchived(false).efficiency(1.0)
                .build();
        userRepository.save(userA);

        userB = User.builder()
                .firstName("UB").lastName("B")
                .email("ub@t.test").password("x")
                .birthDate(LocalDateTime.now().minusYears(20))
                .hiredAt(LocalDateTime.now())
                .userRole(null).address("").phoneNumber("")
                .allowNotifications(true).isArchived(false).efficiency(1.0)
                .build();
        userRepository.save(userB);

        // team member only for userA
        TeamMember tm = new TeamMember();
        tm.setTeam(team);
        tm.setUser(userA);
        teamRepository.findById(team.getId()).ifPresent(t -> {
            team.getTeamMembers().add(tm);
            teamRepository.save(team);
        });

        // seed task
        taskA = new Task();
        taskA.setName("TaskA");
        taskA.setTaskProgress(TaskProgress.NOT_STARTED);
        taskA.setTeam(team);
        taskA.setStartDate(LocalDateTime.now().minusDays(1));
        taskA.setEndDate(LocalDateTime.now().plusDays(1));
        taskRepository.save(taskA);

        // assign userA → userTask
        UserTask ut = new UserTask();
        ut.setTask(taskA);
        ut.setUser(userA);
        userTaskRepository.save(ut);
    }

    @Test @DisplayName("findAll(Pageable) paginates")
    void testFindAllPageable() {
        Page<UserTask> page = userTaskRepository.findAll(PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test @DisplayName("findActiveTasksByUserId returns only for that user")
    void testFindActiveTasksByUserId() {
        List<Task> tasks = userTaskRepository.findActiveTasksByUserId(userA.getId());
        assertThat(tasks).containsExactly(taskA);

        // userB has none
        assertThat(userTaskRepository.findActiveTasksByUserId(userB.getId())).isEmpty();
    }

    @Test @DisplayName("findUsersByTaskId returns all assigned users")
    void testFindUsersByTaskId() {
        List<User> users = userTaskRepository.findUsersByTaskId(taskA.getId());
        assertThat(users).containsExactly(userA);
    }

    @Test @DisplayName("findActiveTasksEndingWithinNextDay filters by dates and statuses")
    void testFindActiveTasksEndingWithinNextDay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = now.plusDays(2);

        // exclude NOT_STARTED and COMPLETED
        List<TaskProgress> excluded = List.of(TaskProgress.NOT_STARTED, TaskProgress.COMPLETED);
        List<UserTask> uts = userTaskRepository.findActiveTasksEndingWithinNextDay(now, deadline, excluded);

        // taskA: endDate within window and status NOT_STARTED → excluded, so no results
        assertThat(uts).isEmpty();
    }
}