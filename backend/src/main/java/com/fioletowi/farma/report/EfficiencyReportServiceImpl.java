package com.fioletowi.farma.report;

import com.fioletowi.farma.task.TaskProgress;
import com.fioletowi.farma.task.TaskRepository;
import com.fioletowi.farma.team.Team;
import com.fioletowi.farma.team.TeamMemberRepository;
import com.fioletowi.farma.team.TeamRepository;
import com.fioletowi.farma.user.User;
import com.fioletowi.farma.user.UserRepository;
import com.fioletowi.farma.user.UserRole;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service implementation providing efficiency reports for workers, leaders, and teams.
 * <p>
 * Generates paginated reports with statistics about task completions, failures,
 * team memberships, and calculates efficiency rates over specified date ranges.
 * Supports filtering by user or team names.
 * </p>
 */
@Service
@AllArgsConstructor
public class EfficiencyReportServiceImpl implements EfficiencyReportService {

    private final TaskRepository taskRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    /**
     * Formats the provided {@link LocalDateTime} to a string in "dd.MM.yyyy" format.
     *
     * @param dt the date-time to format, may be null
     * @return formatted date string or empty string if input is null
     */
    private String fmt(LocalDateTime dt) {
        return dt == null ? "" : dt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    /**
     * Generates a paginated report of worker efficiency within a given date range.
     * <p>
     * The report includes counts of accepted, terminated, and failed tasks,
     * number of teams the worker belongs to, and efficiency rate calculated as
     * tasks completed / (tasks completed + failed tasks).
     * </p>
     * Filtering by worker name is supported; use "all" to disable filtering.
     *
     * @param from     start date/time (inclusive)
     * @param to       end date/time (inclusive)
     * @param filter   filter by worker's first or last name, or "all" for no filtering
     * @param pageable pagination information for page size and number
     * @return paginated list of {@link WorkerReportResponse} objects
     */
    @Override
    public Page<WorkerReportResponse> reportWorkers(LocalDateTime from, LocalDateTime to, String filter, Pageable pageable) {
        List<User> users = "all".equalsIgnoreCase(filter)
                ? userRepository.findAllByUserRole(UserRole.WORKER)
                : userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(filter, filter);

        var list = users.stream().map(u -> {
            long ac = taskRepository.countByUserAndStatus(u.getId(), TaskProgress.COMPLETED_ACCEPTED, from, to);
            long tr = taskRepository.countByUserAndStatus(u.getId(), TaskProgress.COMPLETED_TERMINATED, from, to);
            long fa = taskRepository.countByUserAndStatus(u.getId(), TaskProgress.FAILED, from, to);
            long teams = teamMemberRepository.countByUserId(u.getId());
            long tasks = ac + tr;
            double rate = (tasks + fa) > 0 ? (double) tasks / (tasks + fa) : 0;
            return WorkerReportResponse.builder()
                    .userId(u.getId())
                    .fullName(u.getFirstName() + " " + u.getLastName())
                    .status(u.getStatus().name())
                    .hiredAt(fmt(u.getHiredAt()))
                    .teamCount(teams)
                    .acceptedCount(ac)
                    .terminatedCount(tr)
                    .failedCount(fa)
                    .tasksCount(tasks)
                    .efficiencyRate(rate)
                    .build();
        }).toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), list.size());
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }

    /**
     * Generates a paginated report of leader efficiency within a given date range.
     * <p>
     * The report includes counts of accepted, terminated, and failed tasks assigned to the leader,
     * number of teams led, number of employees under the leader, and efficiency rate.
     * Supports filtering by leader's full name; use "all" for no filtering.
     * </p>
     *
     * @param from     start date/time (inclusive)
     * @param to       end date/time (inclusive)
     * @param filter   filter by leader's full name, or "all" for no filtering
     * @param pageable pagination information for page size and number
     * @return paginated list of {@link LeaderReportResponse} objects
     */
    @Override
    public Page<LeaderReportResponse> reportLeaders(LocalDateTime from, LocalDateTime to, String filter, Pageable pageable) {
        List<Team> teams = teamRepository.findAll();
        Set<User> leaders = teams.stream()
                .map(Team::getLeader)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (!"all".equalsIgnoreCase(filter)) {
            String f = filter.toLowerCase();
            leaders = leaders.stream()
                    .filter(u -> (u.getFirstName() + " " + u.getLastName()).toLowerCase().contains(f))
                    .collect(Collectors.toSet());
        }

        var list = leaders.stream().map(l -> {
            long ac = taskRepository.countByLeaderAndStatus(l.getId(), TaskProgress.COMPLETED_ACCEPTED, from, to);
            long tr = taskRepository.countByLeaderAndStatus(l.getId(), TaskProgress.COMPLETED_TERMINATED, from, to);
            long fa = taskRepository.countByLeaderAndStatus(l.getId(), TaskProgress.FAILED, from, to);
            long teamsCount = teams.stream().filter(t -> l.getId().equals(t.getLeader().getId())).count();
            long employeesCount = teamMemberRepository.countDistinctUsersByLeaderId(l.getId());
            long tasks = ac + tr;
            double rate = (tasks + fa) > 0 ? (double) tasks / (tasks + fa) : 0;
            return LeaderReportResponse.builder()
                    .leaderId(l.getId())
                    .fullName(l.getFirstName() + " " + l.getLastName())
                    .status(l.getStatus().name())
                    .hiredAt(fmt(l.getHiredAt()))
                    .teamsCount(teamsCount)
                    .employeesCount(employeesCount)
                    .acceptedCount(ac)
                    .terminatedCount(tr)
                    .failedCount(fa)
                    .tasksCount(tasks)
                    .efficiencyRate(rate)
                    .build();
        }).toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), list.size());
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }

    /**
     * Generates a paginated report of team efficiency within a given date range.
     * <p>
     * The report includes counts of accepted, terminated, and failed tasks by team,
     * number of team members, and calculated efficiency rate.
     * Supports filtering by team name; use "all" for no filtering.
     * </p>
     *
     * @param from     start date/time (inclusive)
     * @param to       end date/time (inclusive)
     * @param filter   filter by team name, or "all" for no filtering
     * @param pageable pagination information for page size and number
     * @return paginated list of {@link TeamReportResponse} objects
     */
    @Override
    public Page<TeamReportResponse> reportTeams(LocalDateTime from, LocalDateTime to, String filter, Pageable pageable) {
        List<Team> teams = "all".equalsIgnoreCase(filter)
                ? teamRepository.findAll()
                : teamRepository.findByNameContainingIgnoreCase(filter);

        var list = teams.stream().map(t -> {
            long ac = taskRepository.countByTeamAndStatus(t.getId(), TaskProgress.COMPLETED_ACCEPTED, from, to);
            long tr = taskRepository.countByTeamAndStatus(t.getId(), TaskProgress.COMPLETED_TERMINATED, from, to);
            long fa = taskRepository.countByTeamAndStatus(t.getId(), TaskProgress.FAILED, from, to);
            long members = teamMemberRepository.countByTeamId(t.getId());
            long tasks = ac + tr;
            double rate = (tasks + fa) > 0 ? (double) tasks / (tasks + fa) : 0;
            return TeamReportResponse.builder()
                    .teamId(t.getId())
                    .teamName(t.getName())
                    .leaderName(Optional.ofNullable(t.getLeader())
                            .map(u -> u.getFirstName() + " " + u.getLastName())
                            .orElse(""))
                    .membersCount(members)
                    .acceptedCount(ac)
                    .terminatedCount(tr)
                    .failedCount(fa)
                    .tasksCount(tasks)
                    .efficiencyRate(rate)
                    .build();
        }).toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), list.size());
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }
}
