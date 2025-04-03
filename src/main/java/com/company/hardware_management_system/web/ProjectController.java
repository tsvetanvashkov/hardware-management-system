package com.company.hardware_management_system.web;

import com.company.hardware_management_system.department.model.Department;
import com.company.hardware_management_system.exception.DomainException;
import com.company.hardware_management_system.project.model.Project;
import com.company.hardware_management_system.project.repository.ProjectRepository;
import com.company.hardware_management_system.project.service.ProjectService;
import com.company.hardware_management_system.security.AuthenticationMetadata;
import com.company.hardware_management_system.user.model.User;
import com.company.hardware_management_system.user.service.UserService;
import com.company.hardware_management_system.web.dto.AddProjectRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.type.descriptor.sql.internal.NativeEnumDdlTypeImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/projects")
@Slf4j
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final UserService userService;

    public ProjectController(ProjectService projectService, ProjectRepository projectRepository, UserService userService) {
        this.projectService = projectService;
        this.projectRepository = projectRepository;
        this.userService = userService;
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

    @GetMapping("/{projectId}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getEditProjectPage(@PathVariable UUID projectId) {

        Project project = projectService.getById(projectId);
        List<User> allUsers = userService.getAllUsers();

        AddProjectRequest editProjectRequest = AddProjectRequest.builder()
                .name(project.getName())
                .description(project.getDescription())
                .build();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("edit-project");
        modelAndView.addObject("project", project);
        modelAndView.addObject("allUsers", allUsers);
        modelAndView.addObject("editProjectRequest", editProjectRequest);

        return modelAndView;
    }

    @PostMapping("/{projectId}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView updateProject(
            @PathVariable UUID projectId,
            @Valid @ModelAttribute("editProjectRequest") AddProjectRequest addProjectRequest,
            BindingResult bindingResult,
            @RequestParam(required = false) List<UUID> userIds) {

        Project project = projectService.getById(projectId);

        if (bindingResult.hasErrors()) {
            List<User> allUsers = userService.getAllUsers();

            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("edit-project");
            modelAndView.addObject("project", project);
            modelAndView.addObject("allUsers", allUsers);
            return modelAndView;
        }

        // Update project details
        project.setName(addProjectRequest.getName());
        project.setDescription(addProjectRequest.getDescription());

        // Update users
        if (userIds != null && !userIds.isEmpty()) {
            List<User> users = userIds.stream()
                    .map(userService::getById)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            project.setUsers(users);
        } else {
            project.setUsers(Collections.emptyList());
        }

        projectRepository.save(project);
        return new ModelAndView("redirect:/projects");
    }
}
