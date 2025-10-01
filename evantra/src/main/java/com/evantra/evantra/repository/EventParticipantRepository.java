package com.evantra.evantra.repository;

import com.evantra.evantra.model.Event;
import com.evantra.evantra.model.EventParticipant;
import com.evantra.evantra.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, UUID> {

    // Checks if a user is already registered for an event
    boolean existsByEventAndUser(Event event, User user);

    // Counts all participants for a given event ID
    long countByEvent_EventId(UUID eventId);

    @Query("SELECT COUNT(ep) FROM EventParticipant ep WHERE ep.event.eventId = :eventId AND ep.checkedIn = :checkedIn")
    long countByEvent_EventIdAndCheckedIn(@Param("eventId") UUID eventId, @Param("checkedIn") boolean checkedIn);

}
