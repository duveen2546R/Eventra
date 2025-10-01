package com.evantra.evantra.repository;

import com.evantra.evantra.model.EventOrganizer;
import com.evantra.evantra.model.EventOrganizerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventOrganizerRepository extends JpaRepository<EventOrganizer, EventOrganizerId> {
}