package com.evantra.evantra.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterParticipantResponse {

    private String message;
    private String passId;
    private String qrCodeUrl;
    private boolean checkedIn;
}