package com.company.hardware_management_system;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.*;

import com.company.hardware_management_system.project.model.Project;
import com.company.hardware_management_system.project.repository.ProjectRepository;
import com.company.hardware_management_system.project.service.ProjectService;
import com.company.hardware_management_system.user.model.User;
import com.company.hardware_management_system.user.service.UserService;
import com.company.hardware_management_system.web.ProjectController;
import com.company.hardware_management_system.web.dto.AddProjectRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProjectController.class)
@Import(ProjectControllerTest.TestConfig.class)
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserService userService;

    static class TestConfig {
        @Bean
        ProjectService projectService() {
            return mock(ProjectService.class);
        }

        @Bean
        ProjectRepository projectRepository() {
            return mock(ProjectRepository.class);
        }

        @Bean
        UserService userService() {
            return mock(UserService.class);
        }
    }

    // Original test cases from first answer adapted to new approach:

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllProjects_shouldReturnProjectsView() throws Exception {
        List<Project> projects = List.of(new Project(), new Project());
        when(projectService.getAllProjects()).thenReturn(projects);

        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(view().name("projects"))
                .andExpect(model().attributeExists("projects"))
                .andExpect(model().attribute("projects", projects));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getAllProjects_withNonAdminUser_shouldBeForbidden() throws Exception {
        mockMvc.perform(get("/projects"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAddProjectPage_shouldReturnAddProjectView() throws Exception {
        mockMvc.perform(get("/projects/add-project"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-project"))
                .andExpect(model().attributeExists("addProjectRequest"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addProject_withValidRequest_shouldRedirectToProjects() throws Exception {
        AddProjectRequest request = new AddProjectRequest("Test Project", "Test Description");
        Project savedProject = Project.builder().name(request.getName()).description(request.getDescription()).build();

        when(projectService.addProject(any())).thenReturn(savedProject);

        mockMvc.perform(post("/projects/add-project")
                        .with(csrf())
                        .param("name", request.getName())
                        .param("description", request.getDescription()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects"));

        verify(projectRepository).save(savedProject);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addProject_withInvalidRequest_shouldReturnToAddProjectView() throws Exception {
        mockMvc.perform(post("/projects/add-project")
                        .with(csrf())
                        .param("name", "")
                        .param("description", "Test Description"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-project"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEditProjectPage_shouldReturnEditProjectViewWithData() throws Exception {
        UUID projectId = UUID.randomUUID();

        // Create a user list with actual users (not empty)
        List<User> users = List.of(
                User.builder().id(UUID.randomUUID()).email("user1@example.com").build(),
                User.builder().id(UUID.randomUUID()).email("user2@example.com").build()
        );

        // Build project with users
        Project project = Project.builder()
                .id(projectId)
                .name("Test")
                .description("Desc")
                .users(users)
                .build();

        when(projectService.getById(projectId)).thenReturn(project);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/projects/{projectId}/edit", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-project"))
                .andExpect(model().attributeExists("project"))
                .andExpect(model().attributeExists("allUsers"))
                .andExpect(model().attributeExists("editProjectRequest"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProject_withValidRequest_shouldUpdateAndRedirect() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        // Create test project with initialized users collection
        Project project = Project.builder()
                .id(projectId)
                .name("Old Name")
                .description("Old Description")
                .users(new ArrayList<>())
                .build();

        // Create test users
        User user1 = User.builder().id(userId1).email("user1@test.com").build();
        User user2 = User.builder().id(userId2).email("user2@test.com").build();

        // Mock service responses
        when(projectService.getById(projectId)).thenReturn(project);
        when(userService.getById(userId1)).thenReturn(user1);
        when(userService.getById(userId2)).thenReturn(user2);
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        // Create the command object that would be bound from the form
        AddProjectRequest request = new AddProjectRequest();
        request.setName("Updated Name");
        request.setDescription("Updated Desc");

        mockMvc.perform(post("/projects/{projectId}/edit", projectId)
                        .with(csrf())
                        .flashAttr("editProjectRequest", request)  // Match @ModelAttribute name
                        .param("userIds", userId1.toString(), userId2.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects"));

        // Verify interactions
        verify(project).setName("Updated Name");
        verify(project).setDescription("Updated Desc");
        verify(projectRepository).save(project);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProject_withInvalidRequest_shouldReturnToEditView() throws Exception {
        UUID projectId = UUID.randomUUID();

        // Create properly initialized Project with empty users list
        Project project = Project.builder()
                .id(projectId)
                .name("")
                .description("Updated Desc")
                .users(new ArrayList<>()) // Initialize empty collection
                .build();

        // Create proper User objects with required fields
        List<User> users = List.of(
                User.builder()
                        .id(UUID.randomUUID())
                        .email("user1@example.com")
                        .firstName("John")
                        .lastName("Doe")
                        .build(),
                User.builder()
                        .id(UUID.randomUUID())
                        .email("user2@example.com")
                        .firstName("Jane")
                        .lastName("Smith")
                        .build()
        );

        when(projectService.getById(projectId)).thenReturn(project);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(post("/projects/{projectId}/edit", projectId)
                        .with(csrf())
                        .param("name", "") // Invalid empty name
                        .param("description", "Updated Desc"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-project"))
                .andExpect(model().attributeExists("project"))
                .andExpect(model().attributeExists("allUsers"))
                .andExpect(model().attributeExists("editProjectRequest"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProject_withNoUserIds_shouldClearUsers() throws Exception {
        UUID projectId = UUID.randomUUID();

        List<User> users = List.of(
                User.builder()
                        .id(UUID.randomUUID())
                        .email("user1@example.com")
                        .firstName("John")
                        .lastName("Doe")
                        .build(),
                User.builder()
                        .id(UUID.randomUUID())
                        .email("user2@example.com")
                        .firstName("Jane")
                        .lastName("Smith")
                        .build()
        );

        Project project = Project.builder()
                .id(projectId)
                .users(users)
                .build();

        // Mock service responses
        when(projectService.getById(projectId)).thenReturn(project);
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        // Create the command object
        AddProjectRequest request = new AddProjectRequest();
        request.setName("Updated Name");
        request.setDescription("Updated Desc");

        mockMvc.perform(post("/projects/{projectId}/edit", projectId)
                        .with(csrf())  // Add CSRF token
                        .flashAttr("editProjectRequest", request)  // Add command object
                        .param("name", "Updated Name")
                        .param("description", "Updated Desc"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects"));

        // Verify interactions
        verify(project).setName("Updated Name");
        verify(project).setDescription("Updated Desc");
        verify(project).setUsers(Collections.emptyList()); // Verify users were cleared
        verify(projectRepository).save(project);
    }
}
