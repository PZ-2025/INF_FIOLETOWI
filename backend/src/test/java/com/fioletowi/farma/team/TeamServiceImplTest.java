package com.fioletowi.farma.team;

import com.fioletowi.farma.exception.ResourceNotFoundException;
import com.fioletowi.farma.mapper.Mapper;
import com.fioletowi.farma.user.User;
import com.fioletowi.farma.user.UserRepository;
import com.fioletowi.farma.user.UserResponse;
import com.fioletowi.farma.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;


@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class TeamServiceImplTest {

    @Mock TeamRepository teamRepository;
    @Mock Mapper<Team, TeamResponse> teamMapper;
    @Mock UserRepository userRepository;
    @Mock Mapper<User, UserResponse> userMapper;

    @InjectMocks TeamServiceImpl teamService;

    Team sampleTeam;
    TeamResponse sampleResp;
    User sampleManager;
    UserResponse sampleMgrResp;

    @BeforeEach
    void setUp() {
        sampleTeam = new Team();
        sampleTeam.setId(10L);
        sampleTeam.setName("Alpha");
        sampleResp = new TeamResponse();
        sampleResp.setId(10L);
        sampleResp.setName("Alpha");

        sampleManager = User.builder()
                .id(20L)
                .firstName("Mngr")
                .lastName("One")
                .email("mgr@test")
                .password("x")
                .birthDate(LocalDateTime.now().minusYears(30))
                .hiredAt(LocalDateTime.now())
                .userRole(UserRole.MANAGER)
                .address("A").phoneNumber("P")
                .allowNotifications(true)
                .isArchived(false)
                .efficiency(1.0)
                .build();
        sampleMgrResp = new UserResponse();
        sampleMgrResp.setId(20L);
    }

    @Test
    void findAllTeams() {
        var page = new PageImpl<>(List.of(sampleTeam));
        given(teamRepository.findAll(any(Pageable.class))).willReturn(page);
        given(teamMapper.mapTo(sampleTeam, TeamResponse.class)).willReturn(sampleResp);

        var out = teamService.findAllTeams(PageRequest.of(0,5));
        assertThat(out.getTotalElements()).isEqualTo(1);
        assertThat(out.getContent().get(0).getName()).isEqualTo("Alpha");
    }

    @Test
    void findTeamById_found() {
        given(teamRepository.findById(10L)).willReturn(Optional.of(sampleTeam));
        given(teamMapper.mapTo(sampleTeam, TeamResponse.class)).willReturn(sampleResp);

        var resp = teamService.findTeamById(10L);
        assertThat(resp.getId()).isEqualTo(10L);
    }

    @Test
    void findTeamById_notFound() {
        given(teamRepository.findById(5L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> teamService.findTeamById(5L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createTeam() {
        NewTeamRequest req = NewTeamRequest.builder().name("NewTeam").build();
        Team saved = new Team();
        saved.setId(2L);
        saved.setName("NewTeam");
        saved.setCreatedAt(LocalDateTime.now());
        saved.setUpdatedAt(LocalDateTime.now());

        given(teamRepository.save(any(Team.class))).willReturn(saved);
        TeamResponse resp = TeamResponse.builder().id(2L).name("NewTeam").build();
        given(teamMapper.mapTo(saved, TeamResponse.class)).willReturn(resp);

        TeamResponse out = teamService.createTeam(req);
        assertThat(out).isNotNull();
        assertThat(out.getId()).isEqualTo(2L);
        assertThat(out.getName()).isEqualTo("NewTeam");
        assertThat(out.getLeader()).isNull();
    }

    @Test
    void createTeam_leaderNotManager() {
        sampleManager.setUserRole(UserRole.WORKER);
        var req = NewTeamRequest.builder().name("C").leaderId(20L).build();
        given(userRepository.findById(20L)).willReturn(Optional.of(sampleManager));
        assertThatThrownBy(() -> teamService.createTeam(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("is not a manager");
    }

    @Test
    void partialUpdateTeam() {
        var req = UpdateTeamRequest.builder().name("NewName").build();
        given(teamRepository.findById(10L)).willReturn(Optional.of(sampleTeam));
        sampleTeam.setName("NewName");
        given(teamRepository.save(sampleTeam)).willReturn(sampleTeam);
        given(teamMapper.mapTo(sampleTeam, TeamResponse.class)).willReturn(sampleResp);

        var resp = teamService.partialUpdateTeam(10L, req);
        // mapper zwraca sampleResp, więc name w nim jest nadal "Alpha"
        assertThat(resp.getName()).isEqualTo("Alpha");
        then(teamRepository).should().save(argThat(t -> t.getName().equals("NewName")));
    }

    @Test
    void deleteTeam_notFound() {
        given(teamRepository.existsById(5L)).willReturn(false);
        assertThatThrownBy(() -> teamService.deleteTeam(5L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteTeam_success() {
        long id = 10L;
        given(teamRepository.existsById(id)).willReturn(true);

        teamService.deleteTeam(id);

        then(teamRepository).should().deleteById(id);
    }
}
