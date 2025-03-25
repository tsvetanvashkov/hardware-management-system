package com.company.hardware_management_system.web;

import com.company.hardware_management_system.department.model.Department;
import com.company.hardware_management_system.department.repository.DepartmentRepository;
import com.company.hardware_management_system.department.service.DepartmentService;
import com.company.hardware_management_system.office.model.Office;
import com.company.hardware_management_system.security.AuthenticationMetadata;
import com.company.hardware_management_system.web.dto.AddDepartmentRequest;
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
@RequestMapping("/departments")
@Slf4j
public class DepartmentController {

    private final DepartmentService departmentService;
    private final DepartmentRepository departmentRepository;

    public DepartmentController(DepartmentService departmentService, DepartmentRepository departmentRepository) {
        this.departmentService = departmentService;
        this.departmentRepository = departmentRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAllDepartments(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        List<Department> departments = departmentService.getAllDepartments();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("departments");
        modelAndView.addObject("departments", departments);

        log.info("Navigating to departments page.");
        return modelAndView;
    }

    @GetMapping("/add-department")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAddDepartmentPage() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-department");
        modelAndView.addObject("addDepartmentRequest", new AddDepartmentRequest());

        log.info("Navigating to add department page.");
        return modelAndView;
    }

    @PostMapping("/add-department")
    public ModelAndView addDepartment(@Valid AddDepartmentRequest addDepartmentRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("add-department");
        }

        Department department = departmentService.addDepartment(addDepartmentRequest);
        departmentRepository.save(department);
        log.info("Successfully added department to database.");

        return new ModelAndView("redirect:/departments");
    }
}
