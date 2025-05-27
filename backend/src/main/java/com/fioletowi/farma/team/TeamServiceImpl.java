package com.fioletowi.farma.team;

import com.fioletowi.farma.exception.ResourceNotFoundException;
import com.fioletowi.farma.mapper.Mapper;
import com.fioletowi.farma.user.User;
import com.fioletowi.farma.user.UserRepository;
import com.fioletowi.farma.user.UserResponse;
import com.fioletowi.farma.user.UserRole;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Implementation of {@link TeamService} interface to handle business logic for teams.
 */
@Service
@AllArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final Mapper<Team, TeamResponse> teamMapper;
    private final UserRepository userRepository;
    private final Mapper<User, UserResponse> userMapper;

    /**
     * Retrieves a paginated list of all teams with their leaders mapped.
     *
     * @param pageable pagination information
     * @return paginated list of {@link TeamResponse}
     */
    @Override
    public Page<TeamResponse> findAllTeams(Pageable pageable) {
        return teamRepository.findAll(pageable)
                .map(team -> {
                    TeamResponse response = teamMapper.mapTo(team, TeamResponse.class);
                    if (team.getLeader() != null) {
                        response.setLeader(userMapper.mapTo(team.getLeader(), UserResponse.class));
                    }
                    return response;
                });
    }

    /**
     * Finds a team by its ID.
     *
     * @param id the ID of the team
     * @return the found {@link TeamResponse}
     * @throws ResourceNotFoundException if team with given ID does not exist
     */
    @Override
    public TeamResponse findTeamById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team with id " + id + " not found"));
        TeamResponse response = teamMapper.mapTo(team, TeamResponse.class);
        if (team.getLeader() != null) {
            response.setLeader(userMapper.mapTo(team.getLeader(), UserResponse.class));
        }
        return response;
    }

    /**
     * Returns the total number of teams.
     *
     * @return {@link TeamCountResponse} containing the count
     */
    @Override
    public TeamCountResponse getNumberOfTeams() {
        return TeamCountResponse.builder()
                .count(teamRepository.count())
                .build();
    }

    /**
     * Creates a new team based on the provided request.
     * Validates that the leader is a user with the role MANAGER.
     *
     * @param newTeamRequest data for creating the team
     * @return the created {@link TeamResponse}
     * @throws ResourceNotFoundException if leader user is not found
     * @throws IllegalArgumentException  if the leader user is not a manager
     */
    @Override
    public TeamResponse createTeam(NewTeamRequest newTeamRequest) {
        Team team = new Team();
        team.setName(newTeamRequest.getName());
        if (newTeamRequest.getLeaderId() != null) {
            User leader = userRepository.findById(newTeamRequest.getLeaderId())
                    .orElseThrow(() -> new ResourceNotFoundException("User with id " + newTeamRequest.getLeaderId() + " not found"));
            if(leader.getUserRole() != UserRole.MANAGER) {
                throw new IllegalArgumentException("User with id: " + leader.getId() + " is not a manager");
            }
            team.setLeader(leader);
        }
        team.setCreatedAt(LocalDateTime.now());
        team.setUpdatedAt(LocalDateTime.now());
        Team savedTeam = teamRepository.save(team);
        TeamResponse response = teamMapper.mapTo(savedTeam, TeamResponse.class);
        if (savedTeam.getLeader() != null) {
            response.setLeader(userMapper.mapTo(savedTeam.getLeader(), UserResponse.class));
        }
        return response;
    }

    /**
     * Partially updates an existing team.
     *
     * @param id                the ID of the team to update
     * @param updateTeamRequest request containing update information
     * @return updated {@link TeamResponse}
     * @throws ResourceNotFoundException if team or leader user not found
     */
    @Override
    public TeamResponse partialUpdateTeam(Long id, UpdateTeamRequest updateTeamRequest) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team with id " + id + " not found"));

        Optional.ofNullable(updateTeamRequest.getName()).ifPresent(team::setName);
        if (updateTeamRequest.getLeaderId() != null) {
            User leader = userRepository.findById(updateTeamRequest.getLeaderId())
                    .orElseThrow(() -> new ResourceNotFoundException("User with id " + updateTeamRequest.getLeaderId() + " not found"));
            team.setLeader(leader);
        }

        Team updatedTeam = teamRepository.save(team);
        TeamResponse response = teamMapper.mapTo(updatedTeam, TeamResponse.class);
        if (updatedTeam.getLeader() != null) {
            response.setLeader(userMapper.mapTo(updatedTeam.getLeader(), UserResponse.class));
        }
        return response;
    }

    /**
     * Deletes a team by ID.
     *
     * @param id the ID of the team to delete
     * @throws ResourceNotFoundException if the team does not exist
     */
    @Override
    public void deleteTeam(Long id) {
        if (!teamRepository.existsById(id)) {
            throw new ResourceNotFoundException("Team with id " + id + " not found");
        }
        teamRepository.deleteById(id);
    }

    /**
     * Counts the number of teams managed by the authenticated user.
     *
     * @param authentication the authentication object containing the current user principal
     * @return {@link TeamCountResponse} with the count of teams managed by the user
     */
    public TeamCountResponse countByLeader(Authentication authentication) {
        User manager = (User)authentication.getPrincipal();
        return TeamCountResponse.builder().count(teamRepository.countByLeaderId(manager.getId())).build();
    }

}
