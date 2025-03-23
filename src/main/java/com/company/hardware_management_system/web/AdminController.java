package com.company.hardware_management_system.web;

import com.company.hardware_management_system.security.AuthenticationMetadata;
import com.company.hardware_management_system.user.model.User;
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
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
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

        log.info("Navigating to Register Page");
        return modelAndView;
    }

    @PostMapping("/add-user")
    public ModelAndView addUser(@Valid AddUserRequest addUserRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("add-user");
        }

        userService.addUser(addUserRequest);

        return new ModelAndView("redirect:/home");
    }
}
