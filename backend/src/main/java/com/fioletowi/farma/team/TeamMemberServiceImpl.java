package com.fioletowi.farma.team;

import com.fioletowi.farma.email.EmailService;
import com.fioletowi.farma.exception.ResourceNotFoundException;
import com.fioletowi.farma.mapper.Mapper;
import com.fioletowi.farma.user.User;
import com.fioletowi.farma.user.UserRepository;
import com.fioletowi.farma.user.UserResponse;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TeamMemberServiceImpl implements TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final Mapper<TeamMember, TeamMemberResponse> teamMemberMapper;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final Mapper<Team, TeamResponse> teamMapper;
    private final Mapper<User, UserResponse> userMapper;
    private final EmailService emailService;

    /**
     * Retrieves all team members in a paginated format.
     *
     * @param pageable pagination information
     * @return paginated TeamMemberResponse list
     */
    @Override
    public Page<TeamMemberResponse> findAllTeamMembers(Pageable pageable) {
        return teamMemberRepository.findAll(pageable)
                .map(teamMember -> {
                    TeamMemberResponse response = teamMemberMapper.mapTo(teamMember, TeamMemberResponse.class);
                    response.setTeam(teamMapper.mapTo(teamMember.getTeam(), TeamResponse.class));
                    response.setUser(userMapper.mapTo(teamMember.getUser(), UserResponse.class));
                    return response;
                });
    }

    /**
     * Finds a team member by ID.
     *
     * @param id team member ID
     * @return the found TeamMemberResponse
     * @throws ResourceNotFoundException if team member is not found
     */
    @Override
    public TeamMemberResponse findTeamMemberById(Long id) {
        TeamMember teamMember = teamMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team member with id " + id + " not found"));
        TeamMemberResponse response = teamMemberMapper.mapTo(teamMember, TeamMemberResponse.class);
        response.setTeam(teamMapper.mapTo(teamMember.getTeam(), TeamResponse.class));
        response.setUser(userMapper.mapTo(teamMember.getUser(), UserResponse.class));
        return response;
    }

    /**
     * Adds a new team member to a team.
     * Sends an email notification to the user upon successful assignment.
     *
     * @param newTeamMemberRequest request containing teamId and userId
     * @return the created TeamMemberResponse
     * @throws ResourceNotFoundException if team or user is not found
     */
    @Override
    public TeamMemberResponse addTeamMember(NewTeamMemberRequest newTeamMemberRequest) {
        TeamMember teamMember = new TeamMember();

        Team team = teamRepository.findById(newTeamMemberRequest.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team with id " + newTeamMemberRequest.getTeamId() + " not found"));

        User user = userRepository.findById(newTeamMemberRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + newTeamMemberRequest.getUserId() + " not found"));

        teamMember.setTeam(team);
        teamMember.setUser(user);
        teamMember.setCreatedAt(LocalDateTime.now());

        TeamMember savedTeamMember = teamMemberRepository.save(teamMember);

        try {
            emailService.sendAssignedToTeamEmail(user, team);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return teamMemberMapper.mapTo(savedTeamMember, TeamMemberResponse.class);
    }

    /**
     * Partially updates a team member's details.
     *
     * @param id the ID of the team member to update
     * @param updateTeamMemberRequest the update request data
     * @return the updated TeamMemberResponse
     * @throws ResourceNotFoundException if the team member, team, or user is not found
     */
    @Override
    public TeamMemberResponse partialUpdateTeamMember(Long id, UpdateTeamMemberRequest updateTeamMemberRequest) {
        TeamMember teamMember = teamMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team member with id " + id + " not found"));

        if (updateTeamMemberRequest.getTeamId() != null) {
            Team team = teamRepository.findById(updateTeamMemberRequest.getTeamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Team with id " + updateTeamMemberRequest.getTeamId() + " not found"));
            teamMember.setTeam(team);
        }
        if (updateTeamMemberRequest.getUserId() != null) {
            User user = userRepository.findById(updateTeamMemberRequest.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User with id " + updateTeamMemberRequest.getUserId() + " not found"));
            teamMember.setUser(user);
        }
        TeamMember updatedTeamMember = teamMemberRepository.save(teamMember);
        TeamMemberResponse response = teamMemberMapper.mapTo(updatedTeamMember, TeamMemberResponse.class);
        response.setTeam(teamMapper.mapTo(updatedTeamMember.getTeam(), TeamResponse.class));
        response.setUser(userMapper.mapTo(updatedTeamMember.getUser(), UserResponse.class));
        return response;
    }

    /**
     * Deletes a team member by ID.
     *
     * @param id the ID of the team member to delete
     * @throws ResourceNotFoundException if the team member does not exist
     */
    @Override
    public void deleteTeamMember(Long id) {
        if (!teamMemberRepository.existsById(id)) {
            throw new ResourceNotFoundException("Team member with id " + id + " not found");
        }
        teamMemberRepository.deleteById(id);
    }

    /**
     * Removes a user from a specified team.
     *
     * @param userId the ID of the user to remove
     * @param teamId the ID of the team
     * @throws ResourceNotFoundException if the user is not a member of the team
     */
    @Override
    public void removeUserFromTeam(Long userId, Long teamId) {
        TeamMember teamMember = teamMemberRepository.findByUserIdAndTeamId(userId, teamId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " is not a member of team with id " + teamId));

        teamMemberRepository.delete(teamMember);
    }

    /**
     * Returns the number of members in a team.
     *
     * @param teamId the ID of the team
     * @return TeamMemberCountResponse containing the count
     */
    @Override
    public TeamMemberCountResponse getTeamMembersNumber(Long teamId) {
        return TeamMemberCountResponse.builder()
                .count(teamMemberRepository.countByTeamId(teamId))
                .build();
    }

    /**
     * Returns all users who are members of a specified team.
     *
     * @param teamId the ID of the team
     * @return list of UserResponse objects representing team members
     */
    @Override
    public List<UserResponse> getTeamMembersByTeamId(Long teamId) {
        List<User> users = teamMemberRepository.findUsersByTeamId(teamId);
        return users.stream().map(user -> userMapper.mapTo(user, UserResponse.class)).toList();
    }

    /**
     * Returns all teams that a user belongs to.
     *
     * @param userId the ID of the user
     * @return list of TeamResponse objects for the user
     */
    @Override
    public List<TeamResponse> getTeamsForUser(Long userId) {
        List<TeamMember> teamMembers = teamMemberRepository.findByUserId(userId);
        return teamMembers.stream()
                .map(TeamMember::getTeam)
                .collect(Collectors.toList()).stream()
                .map(team -> teamMapper.mapTo(team, TeamResponse.class))
                .toList();
    }

    /**
     * Counts distinct users in teams managed by the leader identified in the authentication.
     *
     * @param authentication the current user's authentication object
     * @return TeamMemberCountResponse containing the count of distinct users
     */
    @Override
    public TeamMemberCountResponse countByLeader(Authentication authentication) {
        User manager = (User) authentication.getPrincipal();
        return TeamMemberCountResponse.builder()
                .count(teamMemberRepository.countDistinctUsersByLeaderId(manager.getId()))
                .build();
    }

}
