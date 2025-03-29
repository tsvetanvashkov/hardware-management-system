package com.company.hardware_management_system.web;


import com.company.hardware_management_system.department.service.DepartmentService;
import com.company.hardware_management_system.notification.service.NotificationService;
import com.company.hardware_management_system.office.model.Office;
import com.company.hardware_management_system.office.service.OfficeService;
import com.company.hardware_management_system.security.AuthenticationMetadata;
import com.company.hardware_management_system.user.model.User;
import com.company.hardware_management_system.user.repository.UserRepository;
import com.company.hardware_management_system.user.service.UserService;
import com.company.hardware_management_system.web.dto.AddUserRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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


}
