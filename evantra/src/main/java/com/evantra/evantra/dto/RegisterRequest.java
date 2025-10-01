package com.evantra.evantra.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String phoneNo;
    private String gender;
    private LocalDate dob;
}