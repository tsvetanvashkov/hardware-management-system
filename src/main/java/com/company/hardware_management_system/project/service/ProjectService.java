package com.company.hardware_management_system.project.service;

import com.company.hardware_management_system.exception.DomainException;
import com.company.hardware_management_system.project.model.Project;
import com.company.hardware_management_system.project.repository.ProjectRepository;
import com.company.hardware_management_system.web.dto.AddProjectRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;


    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project addProject(@Valid AddProjectRequest addProjectRequest) {

        Optional<Project> optionalProject = projectRepository.findByName(addProjectRequest.getName());
        if (optionalProject.isPresent()) {
            throw new DomainException("Project [%s] already exist.".formatted(addProjectRequest.getName()));
        }

        Project project = projectRepository.save(initializeProject(addProjectRequest));

        log.info("Successfully create new project with name {}", project.getName());

        return project;
    }

    private Project initializeProject(AddProjectRequest addProjectRequest) {

        return Project.builder()
                .name(addProjectRequest.getName())
                .description(addProjectRequest.getDescription())
                .build();
    }

    public List<Project> getAllProjects() {

        return projectRepository.findAll();
    }
}
