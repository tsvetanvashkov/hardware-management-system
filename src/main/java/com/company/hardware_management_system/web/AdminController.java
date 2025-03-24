package com.company.hardware_management_system.web;

import com.company.hardware_management_system.department.model.Department;
import com.company.hardware_management_system.department.repository.DepartmentRepository;
import com.company.hardware_management_system.department.service.DepartmentService;
import com.company.hardware_management_system.notification.service.NotificationService;
import com.company.hardware_management_system.office.model.Office;
import com.company.hardware_management_system.office.repository.OfficeRepository;
import com.company.hardware_management_system.office.service.OfficeService;
import com.company.hardware_management_system.security.AuthenticationMetadata;
import com.company.hardware_management_system.user.model.User;
import com.company.hardware_management_system.user.repository.UserRepository;
import com.company.hardware_management_system.user.service.UserService;
import com.company.hardware_management_system.web.dto.AddDepartmentRequest;
import com.company.hardware_management_system.web.dto.AddOfficeRequest;
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
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final OfficeService officeService;
    private final OfficeRepository officeRepository;
    private final DepartmentService departmentService;
    private final DepartmentRepository departmentRepository;

    @Autowired
    public AdminController(UserService userService, UserRepository userRepository, NotificationService notificationService, OfficeService officeService, OfficeRepository officeRepository, DepartmentService departmentService, DepartmentRepository departmentRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.officeService = officeService;
        this.officeRepository = officeRepository;
        this.departmentService = departmentService;
        this.departmentRepository = departmentRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAllUsers(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        List<User> users = userService.getAllUsers();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("users");
        modelAndView.addObject("users", users);

        return modelAndView;
    }

//    @GetMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    public ModelAndView getAllUsers(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
//
//        List<User> users = userService.getAllUsers();
//
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("users");
//        modelAndView.addObject("users", users);
//
//        return modelAndView;
//    }

    @GetMapping("/add-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAddUserPage() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-user");
        modelAndView.addObject("addUserRequest", new AddUserRequest());
        modelAndView.addObject("offices", officeService.getAllOffices());
        modelAndView.addObject("departments", departmentService.getAllDepartments());

        log.info("Navigating to add user page");
        return modelAndView;
    }

    @PostMapping("/add-user")
    public ModelAndView addUser(@Valid AddUserRequest addUserRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("add-user");
        }

        User user = userService.addUser(addUserRequest);
        userRepository.save(user);

        notificationService.sendNotification(
                user.getEmail(),
                "[Account Notification] New account was created",
                userService.getEmailBodyForCreatedUser(addUserRequest));

        return new ModelAndView("redirect:/home");
    }

    @GetMapping("/add-office")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAddOfficePage() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-office");
        modelAndView.addObject("addOfficeRequest", new AddOfficeRequest());

        log.info("Navigating to add office Page");
        return modelAndView;
    }

    @PostMapping("/add-office")
    public ModelAndView addOffice(@Valid AddOfficeRequest addOfficeRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("add-office");
        }

        Office office = officeService.addOffice(addOfficeRequest);
        officeRepository.save(office);

        return new ModelAndView("redirect:/home");
    }

    @GetMapping("/add-department")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAddDepartmentPage() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-department");
        modelAndView.addObject("addDepartmentRequest", new AddDepartmentRequest());

        log.info("Navigating to add department Page");
        return modelAndView;
    }

    @PostMapping("/add-department")
    public ModelAndView addDepartment(@Valid AddDepartmentRequest addDepartmentRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("add-department");
        }

        Department department = departmentService.addDepartment(addDepartmentRequest);
        departmentRepository.save(department);

        return new ModelAndView("redirect:/home");
    }
}
