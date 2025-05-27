package com.fioletowi.farma.team;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    /**
     * Retrieves all teams in a paginated fashion.
     *
     * @param pageable pagination information
     * @return a page of teams
     */
    Page<Team> findAll(Pageable pageable);

    /**
     * Finds teams where the name contains the given filter string (case-insensitive).
     *
     * @param teamFilter substring to filter team names
     * @return list of teams matching the filter
     */
    List<Team> findByNameContainingIgnoreCase(String teamFilter);

    /**
     * Finds all teams led by a specific leader.
     *
     * @param leaderId the ID of the team leader
     * @return list of teams managed by the given leader
     */
    List<Team> findAllByLeaderId(Long leaderId);

    /**
     * Counts how many teams are led by the specified leader.
     *
     * @param leaderId the ID of the team leader
     * @return number of teams for the leader
     */
    long countByLeaderId(Long leaderId);

}
