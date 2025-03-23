package com.company.hardware_management_system.project.repository;

import com.company.hardware_management_system.project.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectRepository  extends JpaRepository<Project, UUID> {

}
