package com.evantra.evantra.controller;

import com.evantra.evantra.repository.*;
import com.evantra.evantra.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/event-organizers")
public class EventOrganizerController {
    
    @Autowired
    private EventOrganizerRepository eventOrganizerRepository;

    @GetMapping
    public List<EventOrganizer> getAllEventOrganizers() {
        return eventOrganizerRepository.findAll();
    }
    
    @PostMapping
    public EventOrganizer createEventOrganizer(@RequestBody EventOrganizer eventOrganizer) {
        return eventOrganizerRepository.save(eventOrganizer);
    }
}

