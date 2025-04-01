package com.company.hardware_management_system.web;


import com.company.hardware_management_system.department.model.Department;
import com.company.hardware_management_system.department.service.DepartmentService;
import com.company.hardware_management_system.exception.ConfirmPasswordException;
import com.company.hardware_management_system.notification.service.NotificationService;
import com.company.hardware_management_system.office.model.Office;
import com.company.hardware_management_system.office.service.OfficeService;
import com.company.hardware_management_system.project.model.Project;
import com.company.hardware_management_system.project.service.ProjectService;
import com.company.hardware_management_system.security.AuthenticationMetadata;
import com.company.hardware_management_system.user.model.User;
import com.company.hardware_management_system.user.repository.UserRepository;
import com.company.hardware_management_system.user.service.UserService;
import com.company.hardware_management_system.web.dto.AddUserRequest;
import com.company.hardware_management_system.web.dto.ChangePasswordRequest;
import com.company.hardware_management_system.web.dto.EditProfileRequest;
import com.company.hardware_management_system.web.dto.EditUserRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final OfficeService officeService;
    private final DepartmentService departmentService;
    private final ProjectService projectService;

    @Autowired
    public UserController(UserService userService, UserRepository userRepository,
                          NotificationService notificationService,
                          OfficeService officeService, DepartmentService departmentService, ProjectService projectService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.officeService = officeService;
        this.departmentService = departmentService;
        this.projectService = projectService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAllUsers(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                    @RequestParam(value = "officeError", required = false) boolean officeError) {

        User loginUser = userService.getById(authenticationMetadata.getUserId());
        List<User> users = userService.getAllUsers();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("users");
        modelAndView.addObject("loginUser", loginUser);
        modelAndView.addObject("users", users);
        modelAndView.addObject("offices", officeService.getAllOffices());
        modelAndView.addObject("departments", departmentService.getAllDepartments());
        modelAndView.addObject("officeError", officeError);

        log.info("Navigating to users page.");
        return modelAndView;
    }

    @GetMapping("/validate-office")
    public String validateOfficeSelection(@RequestParam(value = "officeId", required = false) String officeIdStr) {
        if (officeIdStr == null || officeIdStr.isEmpty()) {
            return "redirect:/users?officeError=true";
        }

        try {
            UUID officeId = UUID.fromString(officeIdStr);
            return "redirect:/users/add-user?officeId=" + officeId;
        } catch (IllegalArgumentException e) {
            return "redirect:/users?officeError=true";
        }
    }

    @GetMapping("/add-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAddUserPage(@RequestParam(value = "officeId", required = false) UUID officeId,
                                       @RequestParam(value = "officeError", required = false) boolean officeError) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-user");
        modelAndView.addObject("addUserRequest", new AddUserRequest());
        modelAndView.addObject("offices", officeService.getAllOffices());
        modelAndView.addObject("officeError", officeError);

        // If an office is selected, filter departments
        if (officeId != null) {
            Office office = officeService.getOfficeById(officeId);
            List<Office> offices = new ArrayList<>();
            offices.add(office);

            modelAndView.addObject("departments", departmentService.getAllDepartmentsByOffices(offices));
            modelAndView.addObject("selectedOfficeId", officeId);
        } else {
            modelAndView.addObject("departments", departmentService.getAllDepartments());
        }

        log.info("Navigating to add user page.");
        return modelAndView;
    }

    @PostMapping("/add-user")
    public ModelAndView addUser(@Valid AddUserRequest addUserRequest, BindingResult bindingResult,
                                @RequestParam(value = "officeId", required = false) UUID officeId) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("add-user");
        }

        Office office = officeService.getOfficeById(officeId);

        User user = userService.addUser(addUserRequest, office);
        userRepository.save(user);
        log.info("Successfully added user to database.");

        notificationService.sendNotification(
                user.getEmail(),
                "[Account Notification] New account was created",
                userService.getEmailBodyForCreatedUser(addUserRequest));

        return new ModelAndView("redirect:/users");
    }

    @GetMapping("/{userId}/profile")
    public ModelAndView getProfileMenu(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("edit-profile");
        modelAndView.addObject("user", user);
        modelAndView.addObject("editProfile", EditProfileRequest.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .profilePicture(user.getProfilePicture())
                .build());
        modelAndView.addObject("changePassword", new ChangePasswordRequest());

        log.info("Navigating to profile page.");
        return modelAndView;
    }

    @PatchMapping("/{userId}/profile/details")
    public ModelAndView updateProfile(
            @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
            @Valid @ModelAttribute("editProfile") EditProfileRequest editProfileRequest,
            BindingResult bindingResult) {

        User user = userService.getById(authenticationMetadata.getUserId());

        if (bindingResult.hasErrors()) {
            return prepareProfileView(user,
                    editProfileRequest, new ChangePasswordRequest(),
                    null, null);
        }

        userService.editUserDetails(user, editProfileRequest);
        log.info("Successfully edited user profile in database.");

        return prepareProfileView(user,
                editProfileRequest, new ChangePasswordRequest(),
                "Profile updated successfully!",null);
    }

    @PostMapping("/{userId}/profile/password")
    public ModelAndView changePassword(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                           @Valid ChangePasswordRequest changePasswordRequest, BindingResult bindingResult) {

        User user = userService.getById(authenticationMetadata.getUserId());

        if (!changePasswordRequest.isPasswordConfirmed()) {
            bindingResult.rejectValue("confirmPassword", "match", "Passwords must match");
        }

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = prepareProfileView(user,
                    new EditProfileRequest(), changePasswordRequest,
                    null, null);
            modelAndView.addObject(BindingResult.MODEL_KEY_PREFIX + "changePassword", bindingResult);
            return modelAndView;
        }

        try {
            userService.changeUserPassword(user, changePasswordRequest);
            log.info("Password changed successfully for user ID: {}", user.getId());

            return prepareProfileView(user,
                    new EditProfileRequest(), new ChangePasswordRequest(),
                    null, "Password changed successfully!");
        } catch (ConfirmPasswordException e) {
            ModelAndView modelAndView = prepareProfileView(user,
                    new EditProfileRequest(), changePasswordRequest,
                    null,null);
            modelAndView.addObject("passwordError", e.getMessage());
            return modelAndView;
        }
    }

    private ModelAndView prepareProfileView(User user,
                                            EditProfileRequest editRequest,
                                            ChangePasswordRequest passwordRequest,
                                            String profileSuccess,
                                            String passwordSuccess) {

        ModelAndView modelAndView = new ModelAndView("edit-profile");
        modelAndView.addObject("user", user);
        modelAndView.addObject("editProfile", editRequest);
        modelAndView.addObject("changePassword", passwordRequest);
        if (profileSuccess != null) {
            modelAndView.addObject("profileSuccess", profileSuccess);
        }
        if (passwordSuccess != null) {
            modelAndView.addObject("passwordSuccess", passwordSuccess);
        }
        return modelAndView;
    }

    @ExceptionHandler(ConfirmPasswordException.class)
    public ModelAndView handleConfirmPasswordException(ConfirmPasswordException ex) {
        ModelAndView modelAndView = new ModelAndView("edit-profile");
        modelAndView.addObject("passwordError", ex.getMessage());
        return modelAndView;
    }

    @GetMapping("/{userId}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getEditUserPage(
            @PathVariable("userId") UUID userId,
            @RequestParam(required = false) UUID officeId) {

        User user = userService.getById(userId);
        List<Project> allProjects = projectService.getAllProjects();
        List<Office> allOffices = officeService.getAllOffices();

        // Determine departments to show
        List<Department> departments;
        List<Office> offices = new ArrayList<>();
        if (officeId != null) {
            // If office selected in form, use that
            offices.add(officeService.getOfficeById(officeId));
            departments = departmentService.getAllDepartmentsByOffices(offices);
        } else if (user.getOffice() != null) {
            // Otherwise use user's current office
            offices.add(user.getOffice());
            departments = departmentService.getAllDepartmentsByOffices(offices);
        } else {
            // No office selected or assigned
            departments = departmentService.getAllDepartments();
        }

        ModelAndView modelAndView = new ModelAndView("edit-user");
        modelAndView.addObject("user", user);
        modelAndView.addObject("allProjects", allProjects);
        modelAndView.addObject("allOffices", allOffices);
        modelAndView.addObject("allDepartments", departments);
        modelAndView.addObject("selectedOfficeId", offices != null ? offices.getFirst().getId() : null);

        // Initialize form with user data
        EditUserRequest editUserRequest = EditUserRequest.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .userRole(user.getUserRole())
                .isActive(user.isActive())
                .officeId(offices != null ? offices.getFirst().getId() : null)
                .departmentId(user.getDepartment() != null ? user.getDepartment().getId() : null)
                .build();

        modelAndView.addObject("editUserRequest", editUserRequest);

        return modelAndView;
    }

    @PostMapping("/{userId}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView updateUser(
            @PathVariable UUID userId,
            @Valid EditUserRequest editUserRequest,
            BindingResult bindingResult,
            @RequestParam(required = false) List<UUID> projectIds) {

        User user = userService.getById(userId);

        if (bindingResult.hasErrors())

        // Update user details
        user.setUsername(editUserRequest.getUsername());
        user.setEmail(editUserRequest.getEmail());
        user.setUserRole(editUserRequest.getUserRole());
        user.setActive(editUserRequest.isActive());
        user.setOffice(officeService.getOfficeById(editUserRequest.getOfficeId()));
        user.setDepartment(departmentService.getDepartmentById(editUserRequest.getDepartmentId()));

        // Update projects
        if (projectIds != null && !projectIds.isEmpty()) {
            List<Project> projects = new ArrayList<>();
            for (UUID projectId : projectIds) {
                projects.add(projectService.getById(projectId));
            }
            user.setProjects(projects);
        } else {
            user.setProjects(Collections.emptyList());
        }

        userRepository.save(user);
        return new ModelAndView("redirect:/users");
    }

}
