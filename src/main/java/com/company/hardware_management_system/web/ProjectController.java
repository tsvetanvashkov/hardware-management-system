package com.company.hardware_management_system.web;

import com.company.hardware_management_system.project.model.Project;
import com.company.hardware_management_system.project.repository.ProjectRepository;
import com.company.hardware_management_system.project.service.ProjectService;
import com.company.hardware_management_system.security.AuthenticationMetadata;
import com.company.hardware_management_system.user.model.User;
import com.company.hardware_management_system.web.dto.AddProjectRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/projects")
@Slf4j
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectRepository projectRepository;

    public ProjectController(ProjectService projectService, ProjectRepository projectRepository) {
        this.projectService = projectService;
        this.projectRepository = projectRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAllUsers(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        List<Project> projects = projectService.getAllProjects();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("projects");
        modelAndView.addObject("projects", projects);

        log.info("Navigating to projects page.");
        return modelAndView;
    }

    @GetMapping("/add-project")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAddProjectPage() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-project");
        modelAndView.addObject("addProjectRequest", new AddProjectRequest());

        log.info("Navigating to add project page.");
        return modelAndView;
    }

    @PostMapping("/add-project")
    public ModelAndView addProject(@Valid AddProjectRequest addProjectRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("add-project");
        }

        Project project = projectService.addProject(addProjectRequest);
        projectRepository.save(project);
        log.info("Successfully added project to database.");

        return new ModelAndView("redirect:/projects");
    }
}
