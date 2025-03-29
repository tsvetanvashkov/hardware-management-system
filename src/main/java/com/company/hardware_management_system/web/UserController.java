package com.company.hardware_management_system.web;


import com.company.hardware_management_system.department.service.DepartmentService;
import com.company.hardware_management_system.exception.ConfirmPasswordException;
import com.company.hardware_management_system.notification.service.NotificationService;
import com.company.hardware_management_system.office.model.Office;
import com.company.hardware_management_system.office.service.OfficeService;
import com.company.hardware_management_system.security.AuthenticationMetadata;
import com.company.hardware_management_system.user.model.User;
import com.company.hardware_management_system.user.repository.UserRepository;
import com.company.hardware_management_system.user.service.UserService;
import com.company.hardware_management_system.web.dto.AddUserRequest;
import com.company.hardware_management_system.web.dto.ChangePasswordRequest;
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

    @Autowired
    public UserController(UserService userService, UserRepository userRepository,
                          NotificationService notificationService,
                          OfficeService officeService, DepartmentService departmentService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.officeService = officeService;
        this.departmentService = departmentService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAllUsers(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User loginUser = userService.getById(authenticationMetadata.getUserId());
        List<User> users = userService.getAllUsers();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("users");
        modelAndView.addObject("loginUser", loginUser);
        modelAndView.addObject("users", users);
        modelAndView.addObject("offices", officeService.getAllOffices());
        modelAndView.addObject("departments", departmentService.getAllDepartments());

        log.info("Navigating to users page.");
        return modelAndView;
    }

    @GetMapping("/add-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAddUserPage(@RequestParam(value = "officeId", required = false) UUID officeId) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-user");
        modelAndView.addObject("addUserRequest", new AddUserRequest());
        modelAndView.addObject("offices", officeService.getAllOffices());

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
    public ModelAndView addUser(@Valid AddUserRequest addUserRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("add-user");
        }

        User user = userService.addUser(addUserRequest);
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
        modelAndView.addObject("editUser", EditUserRequest.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .profilePicture(user.getProfilePicture())
                .build());
        modelAndView.addObject("changePassword", new ChangePasswordRequest());

        log.info("Navigating to profile page.");
        return modelAndView;
    }

    @PatchMapping("/{userId}/profile/details")
    public ModelAndView updateProfile(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                          @Valid EditUserRequest editUserRequest, BindingResult bindingResult) {

        User user = userService.getById(authenticationMetadata.getUserId());

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("edit-profile");
            modelAndView.addObject("user", user);
            modelAndView.addObject("editUser", editUserRequest);
            return modelAndView;
        }

        userService.editUserDetails(user, editUserRequest);
        log.info("Successfully edit user profile in database.");

        return new ModelAndView("redirect:/home");
    }

    @PostMapping("/{userId}/profile/password")
    public ModelAndView changePassword(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                           @Valid ChangePasswordRequest changePasswordRequest, BindingResult bindingResult) {

        User user = userService.getById(authenticationMetadata.getUserId());

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("edit-profile");
            modelAndView.addObject("user", user);
            modelAndView.addObject("changePassword", changePasswordRequest);
            return modelAndView;
        }

        userService.changeUserPassword(user, changePasswordRequest);
        log.info("Successfully change user password in database.");

        return new ModelAndView("redirect:/home");
    }

    @ExceptionHandler(ConfirmPasswordException.class)
    public ModelAndView handleConfirmPasswordException(ConfirmPasswordException ex) {
        ModelAndView modelAndView = new ModelAndView("edit-profile");
        modelAndView.addObject("passwordError", ex.getMessage());
        return modelAndView;
    }

}
