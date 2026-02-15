package com.evantra.evantra.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckInResponse {

    private String message;
    private String passId;
    private boolean checkedIn;
}