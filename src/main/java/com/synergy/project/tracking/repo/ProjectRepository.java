package com.synergy.project.tracking.repo;

import com.synergy.project.tracking.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
