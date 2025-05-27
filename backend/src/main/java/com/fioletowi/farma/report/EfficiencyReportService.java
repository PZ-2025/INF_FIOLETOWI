package com.fioletowi.farma.report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;

/**
 * Service interface for generating efficiency reports.
 * Provides methods to retrieve paginated reports for workers, leaders, and teams
 * within a given date-time range, with optional filtering.
 */
public interface EfficiencyReportService {

    /**
     * Generates a paginated report of worker efficiency within the specified date-time range.
     *
     * @param from     the start date-time of the report period
     * @param to       the end date-time of the report period
     * @param filter   a filter to apply to the report (e.g., status or category)
     * @param pageable pagination and sorting information
     * @return a paginated list of worker report responses
     */
    Page<WorkerReportResponse> reportWorkers(LocalDateTime from, LocalDateTime to, String filter, Pageable pageable);

    /**
     * Generates a paginated report of leader efficiency within the specified date-time range.
     *
     * @param from     the start date-time of the report period
     * @param to       the end date-time of the report period
     * @param filter   a filter to apply to the report
     * @param pageable pagination and sorting information
     * @return a paginated list of leader report responses
     */
    Page<LeaderReportResponse> reportLeaders(LocalDateTime from, LocalDateTime to, String filter, Pageable pageable);

    /**
     * Generates a paginated report of team efficiency within the specified date-time range.
     *
     * @param from     the start date-time of the report period
     * @param to       the end date-time of the report period
     * @param filter   a filter to apply to the report
     * @param pageable pagination and sorting information
     * @return a paginated list of team report responses
     */
    Page<TeamReportResponse> reportTeams(LocalDateTime from, LocalDateTime to, String filter, Pageable pageable);

}
