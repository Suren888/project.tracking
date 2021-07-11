package com.synergy.project.tracking.repo;

import com.synergy.project.tracking.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact,Long> {
}
