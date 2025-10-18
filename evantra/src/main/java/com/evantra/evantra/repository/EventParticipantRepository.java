package com.evantra.evantra.repository;

import com.evantra.evantra.model.Event;
import com.evantra.evantra.model.EventParticipant;
import com.evantra.evantra.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, UUID> {

    /**
     * Checks if a user is already registered for an event. Used in the registration flow.
     */
    boolean existsByEventAndUser(Event event, User user);

    /**
     * Counts all participants for a given event ID. Used for the stats endpoint.
     */
    long countByEvent_EventId(UUID eventId);

    /**
     * Counts only the participants who have checked in for a given event ID. Used for the stats endpoint.
     */
    @Query("SELECT COUNT(ep) FROM EventParticipant ep WHERE ep.event.eventId = :eventId AND ep.checkedIn = :checkedIn")
    long countByEvent_EventIdAndCheckedIn(@Param("eventId") UUID eventId, @Param("checkedIn") boolean checkedIn);

    /**
     * NEW METHOD: Finds all Events that a specific User is a participant in.
     * This is used by the "My Events" page when the role is 'participant'.
     */
    @Query("SELECT p.event FROM EventParticipant p WHERE p.user.userId = :userId")
    List<Event> findEventsByParticipantUserId(@Param("userId") UUID userId);

}