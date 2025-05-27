package com.fioletowi.farma.report;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * REST controller for managing administrative efficiency reports.
 * Provides endpoints to fetch paginated reports on workers, leaders, and teams
 * within a specified date range and with optional filtering.
 *
 * Access to all endpoints is restricted to users with roles OWNER or MANAGER.
 */
@RestController
@RequestMapping("/admin/efficiency-raport")
@AllArgsConstructor
public class AdminEfficiencyRaportController {

    private final EfficiencyReportService service;

    /**
     * Retrieves a paginated report of workers' efficiency within the specified date range.
     *
     * @param start   the start date of the report (format: dd.MM.yyyy)
     * @param end     the end date of the report (format: dd.MM.yyyy)
     * @param filter  optional filter criteria; defaults to "all"
     * @param pageable pagination information
     * @return paginated response containing worker efficiency report data
     */
    @GetMapping("/workers")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<Page<WorkerReportResponse>> workers(
            @RequestParam("startDate") @DateTimeFormat(pattern="dd.MM.yyyy") LocalDate start,
            @RequestParam("endDate") @DateTimeFormat(pattern="dd.MM.yyyy") LocalDate end,
            @RequestParam(value="filter", defaultValue="all") String filter,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                service.reportWorkers(start.atStartOfDay(), end.atTime(23,59,59), filter, pageable)
        );
    }

    /**
     * Retrieves a paginated report of leaders' efficiency within the specified date range.
     *
     * @param start   the start date of the report (format: dd.MM.yyyy)
     * @param end     the end date of the report (format: dd.MM.yyyy)
     * @param filter  optional filter criteria; defaults to "all"
     * @param pageable pagination information
     * @return paginated response containing leader efficiency report data
     */
    @GetMapping("/leaders")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<Page<LeaderReportResponse>> leaders(
            @RequestParam("startDate") @DateTimeFormat(pattern="dd.MM.yyyy") LocalDate start,
            @RequestParam("endDate") @DateTimeFormat(pattern="dd.MM.yyyy") LocalDate end,
            @RequestParam(value="filter", defaultValue="all") String filter,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                service.reportLeaders(start.atStartOfDay(), end.atTime(23,59,59), filter, pageable)
        );
    }

    /**
     * Retrieves a paginated report of teams' efficiency within the specified date range.
     *
     * @param start   the start date of the report (format: dd.MM.yyyy)
     * @param end     the end date of the report (format: dd.MM.yyyy)
     * @param filter  optional filter criteria; defaults to "all"
     * @param pageable pagination information
     * @return paginated response containing team efficiency report data
     */
    @GetMapping("/teams")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<Page<TeamReportResponse>> teams(
            @RequestParam("startDate") @DateTimeFormat(pattern="dd.MM.yyyy") LocalDate start,
            @RequestParam("endDate") @DateTimeFormat(pattern="dd.MM.yyyy") LocalDate end,
            @RequestParam(value="filter", defaultValue="all") String filter,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                service.reportTeams(start.atStartOfDay(), end.atTime(23,59,59), filter, pageable)
        );
    }
}
