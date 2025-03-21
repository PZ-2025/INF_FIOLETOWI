package com.example.farm_registration.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class RegistrationRequest {

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private LocalDateTime birthDate;

    private String address;

    private String phoneNumber;

}
