package com.evantra.evantra.repository;

import com.evantra.evantra.model.Checkin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface CheckinRepository extends JpaRepository<Checkin, UUID> {}