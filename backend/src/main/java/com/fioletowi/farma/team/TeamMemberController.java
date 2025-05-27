package com.fioletowi.farma.team;

import com.fioletowi.farma.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller responsible for handling requests related to team memberships.
 */
@RestController
@RequestMapping("/team-members")
@RequiredArgsConstructor
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    /**
     * Endpoint to get all teams associated with the currently authenticated user.
     *
     * @param authentication the Spring Security Authentication object containing the logged-in user details
     * @return a list of teams the user belongs to, wrapped in a ResponseEntity
     */
    @GetMapping
    public ResponseEntity<List<TeamResponse>> getTeamsForUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(teamMemberService.getTeamsForUser(user.getId()));
    }

}
