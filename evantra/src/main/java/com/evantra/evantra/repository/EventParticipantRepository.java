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
    EventParticipant findByEvent_EventIdAndUser_UserId(UUID eventId, UUID userId);
    long countByEvent_EventId(UUID eventId);
    @Query("SELECT count(ep) FROM EventParticipant ep WHERE ep.event.eventId = :eventId AND ep.checkedIn = :checkedIn")
    long countByEvent_EventIdAndCheckedIn(@Param("eventId") UUID eventId, @Param("checkedIn") boolean checkedIn);
    boolean existsByEvent_EventIdAndUser_UserId(UUID eventId, UUID userId);
    boolean existsByEventAndUser(Event event, User user);
    @Query("SELECT e FROM Event e JOIN EventParticipant ep ON e.eventId = ep.event.eventId WHERE ep.user.userId = :userId")
    List<Event> findEventsByParticipantUserId(@Param("userId") UUID userId);

}