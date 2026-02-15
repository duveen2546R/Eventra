package com.evantra.evantra.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class RegisterParticipantRequest {

    private UUID eventId;
    private UUID userId;
}