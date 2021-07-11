package com.synergy.project.tracking.repo;

import com.synergy.project.tracking.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<Status,Integer> {
}
