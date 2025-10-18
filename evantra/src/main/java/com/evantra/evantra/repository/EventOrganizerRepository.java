package com.evantra.evantra.repository;

import com.evantra.evantra.model.Event;
import com.evantra.evantra.model.EventOrganizer;
import com.evantra.evantra.model.EventOrganizerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventOrganizerRepository extends JpaRepository<EventOrganizer, EventOrganizerId> {

    /**
     * Finds all Events that a specific User is an organizer of.
     * The JPQL query traverses the relationship from EventOrganizer -> User -> userId.
     * The @Param annotation is crucial to link the method's 'userId' to the query's ':userId'.
     */
    @Query("SELECT o.event FROM EventOrganizer o WHERE o.user.userId = :userId")
    List<Event> findEventsByOrganizerUserId(@Param("userId") UUID userId);

}