package com.example.farm_registration.user_profile;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private LocalDateTime birthDate;

    private LocalDateTime hiredAt;

    private LocalDateTime terminatedAt;

    private String status;

    private String note;

    private Double efficiency;

    private Boolean isArchived;

    private Boolean allowNotifications;

    private UserRole userRole;

    private String address;

    private String phoneNumber;
}

