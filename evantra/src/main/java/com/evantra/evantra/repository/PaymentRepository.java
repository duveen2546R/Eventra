package com.evantra.evantra.repository;

import com.evantra.evantra.model.Payment;
import com.evantra.evantra.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByUser(User user);
    @Query("SELECT p FROM Payment p JOIN p.event e JOIN EventOrganizer eo ON e.eventId = eo.event.eventId WHERE eo.user = :user")
    List<Payment> findByEvent_EventOrganizers_User(@Param("user") User user);
}