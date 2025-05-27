package com.fioletowi.farma.team;

import com.fioletowi.farma.email.EmailService;
import com.fioletowi.farma.exception.ResourceNotFoundException;
import com.fioletowi.farma.mapper.Mapper;
import com.fioletowi.farma.user.User;
import com.fioletowi.farma.user.UserRepository;
import com.fioletowi.farma.user.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class TeamMemberServiceImplTest {

    @Mock
    TeamMemberRepository teamMemberRepository;

    @Mock
    Mapper<TeamMember, TeamMemberResponse> tmMapper;

    @Mock
    TeamRepository teamRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    Mapper<Team, TeamResponse> teamMapper;

    @Mock
    Mapper<User, UserResponse> userMapper;

    @Mock
    EmailService emailService;

    @InjectMocks
    TeamMemberServiceImpl service;

    private Team team;
    private User user;
    private TeamMember tm;
    private TeamMemberResponse tmResp;
    private TeamResponse teamResp;
    private UserResponse userResp;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // domain objects
        team = new Team();
        team.setId(10L);
        team.setName("Test Team");

        user = new User();
        user.setId(20L);
        user.setEmail("user@test");

        tm = new TeamMember();
        tm.setId(5L);
        tm.setTeam(team);
        tm.setUser(user);
        tm.setCreatedAt(LocalDateTime.now());

        // response DTOs
        tmResp = new TeamMemberResponse();
        tmResp.setId(5L);

        teamResp = new TeamResponse();
        teamResp.setId(10L);

        userResp = new UserResponse();
        userResp.setId(20L);

        // common mapper stubs
        given(tmMapper.mapTo(any(TeamMember.class), eq(TeamMemberResponse.class)))
                .willReturn(tmResp);
        given(teamMapper.mapTo(any(Team.class), eq(TeamResponse.class)))
                .willReturn(teamResp);
        given(userMapper.mapTo(any(User.class), eq(UserResponse.class)))
                .willReturn(userResp);
    }

    @Test
    void findAllTeamMembers_returnsPagedResponses() {
        Page<TeamMember> pageIn = new PageImpl<>(List.of(tm), PageRequest.of(0, 1), 1);
        given(teamMemberRepository.findAll(PageRequest.of(0, 1))).willReturn(pageIn);

        Page<TeamMemberResponse> pageOut = service.findAllTeamMembers(PageRequest.of(0, 1));
        assertThat(pageOut.getTotalElements()).isEqualTo(1);
        TeamMemberResponse r = pageOut.getContent().get(0);
        assertThat(r.getId()).isEqualTo(5L);
        assertThat(r.getTeam().getId()).isEqualTo(10L);
        assertThat(r.getUser().getId()).isEqualTo(20L);
    }

    @Test
    void findTeamMemberById_found() {
        given(teamMemberRepository.findById(5L)).willReturn(Optional.of(tm));

        TeamMemberResponse out = service.findTeamMemberById(5L);
        assertThat(out).isNotNull();
        assertThat(out.getId()).isEqualTo(5L);
        assertThat(out.getTeam().getId()).isEqualTo(10L);
        assertThat(out.getUser().getId()).isEqualTo(20L);
    }

    @Test
    void findTeamMemberById_notFound() {
        given(teamMemberRepository.findById(7L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> service.findTeamMemberById(7L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Team member with id 7 not found");
    }

    @Test
    void addTeamMember_happyPath() throws Exception {
        NewTeamMemberRequest req = new NewTeamMemberRequest(10L, 20L);

        given(teamRepository.findById(10L)).willReturn(Optional.of(team));
        given(userRepository.findById(20L)).willReturn(Optional.of(user));
        given(teamMemberRepository.save(any(TeamMember.class))).willReturn(tm);

        TeamMemberResponse out = service.addTeamMember(req);
        assertThat(out).isNotNull();
        assertThat(out.getId()).isEqualTo(5L);
        then(emailService).should().sendAssignedToTeamEmail(user, team);
    }

    @Test
    void addTeamMember_missingTeam() {
        NewTeamMemberRequest req = new NewTeamMemberRequest(1L, 2L);
        given(teamRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.addTeamMember(req))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Team with id 1 not found");
    }

    @Test
    void partialUpdate_changeTeamAndUser() {
        UpdateTeamMemberRequest upd = new UpdateTeamMemberRequest(10L, 20L);

        given(teamMemberRepository.findById(5L)).willReturn(Optional.of(tm));
        given(teamRepository.findById(10L)).willReturn(Optional.of(team));
        given(userRepository.findById(20L)).willReturn(Optional.of(user));
        given(teamMemberRepository.save(tm)).willReturn(tm);

        TeamMemberResponse out = service.partialUpdateTeamMember(5L, upd);
        assertThat(out).isNotNull();
        assertThat(out.getId()).isEqualTo(5L);
        assertThat(out.getTeam().getId()).isEqualTo(10L);
        assertThat(out.getUser().getId()).isEqualTo(20L);
    }

    @Test
    void partialUpdate_missingMember() {
        UpdateTeamMemberRequest upd = new UpdateTeamMemberRequest(10L, null);
        given(teamMemberRepository.findById(5L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.partialUpdateTeamMember(5L, upd))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Team member with id 5 not found");
    }

    @Test
    void deleteTeamMember_happyPath() {
        given(teamMemberRepository.existsById(5L)).willReturn(true);

        service.deleteTeamMember(5L);
        then(teamMemberRepository).should().deleteById(5L);
    }

    @Test
    void deleteTeamMember_notFound() {
        given(teamMemberRepository.existsById(8L)).willReturn(false);
        assertThatThrownBy(() -> service.deleteTeamMember(8L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Team member with id 8 not found");
    }

    @Test
    void removeUserFromTeam_happyPath() {
        given(teamMemberRepository.findByUserIdAndTeamId(20L, 10L))
                .willReturn(Optional.of(tm));

        service.removeUserFromTeam(20L, 10L);
        then(teamMemberRepository).should().delete(tm);
    }

    @Test
    void removeUserFromTeam_notMember() {
        given(teamMemberRepository.findByUserIdAndTeamId(2L, 3L))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> service.removeUserFromTeam(2L, 3L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User with id 2 is not a member of team with id 3");
    }

    @Test
    void getTeamMembersNumber() {
        given(teamMemberRepository.countByTeamId(10L)).willReturn(7L);

        TeamMemberCountResponse cnt = service.getTeamMembersNumber(10L);
        assertThat(cnt).isNotNull();
        assertThat(cnt.getCount()).isEqualTo(7L);
    }

    @Test
    void getTeamMembersByTeamId_returnsUsers() {
        given(teamMemberRepository.findUsersByTeamId(10L)).willReturn(List.of(user));
        given(userMapper.mapTo(user, UserResponse.class)).willReturn(userResp);

        List<UserResponse> list = service.getTeamMembersByTeamId(10L);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getId()).isEqualTo(20L);
    }

    @Test
    void getTeamsForUser_returnsTeams() {
        given(teamMemberRepository.findByUserId(20L)).willReturn(List.of(tm));
        given(teamMapper.mapTo(team, TeamResponse.class)).willReturn(teamResp);

        List<TeamResponse> list = service.getTeamsForUser(20L);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getId()).isEqualTo(10L);
    }

    @Test
    void countByLeader_returnsDistinctCount() {
        Authentication auth = mock(Authentication.class);
        User manager = new User();
        manager.setId(99L);
        given(auth.getPrincipal()).willReturn(manager);
        given(teamMemberRepository.countDistinctUsersByLeaderId(99L)).willReturn(42L);

        TeamMemberCountResponse resp = service.countByLeader(auth);
        assertThat(resp).isNotNull();
        assertThat(resp.getCount()).isEqualTo(42L);
    }
}
