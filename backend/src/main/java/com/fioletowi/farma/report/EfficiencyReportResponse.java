//package com.fioletowi.farma.report;
//
//import lombok.*;
//
//@Getter
//@Setter
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class EfficiencyReportResponse {
//
//    private Long id;                     // userId / leaderId / teamId
//    private String name;                 // "Jan Kowalski" / "Anna Nowak" / "Green Team"
//    private String type;                 // "WORKER" | "LEADER" | "TEAM"
//    private String status;               // tylko dla WORKER/LEADER: user.status
//    private String hiredAt;              // for WORKER/LEADER: sformatowany
//    private long teamCount;              // dla WORKER: liczba zespołów
//    private long acceptedCount;
//    private long terminatedCount;
//    private long failedCount;
//    private long tasksCount;             // accepted + terminated
//    private double efficiencyRate;       // (accepted+terminated)/(accepted+terminated+failed)
//
//}