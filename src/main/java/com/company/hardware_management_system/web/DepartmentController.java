package com.company.hardware_management_system.web;

import com.company.hardware_management_system.department.model.Department;
import com.company.hardware_management_system.department.repository.DepartmentRepository;
import com.company.hardware_management_system.department.service.DepartmentService;
import com.company.hardware_management_system.exception.DomainException;
import com.company.hardware_management_system.office.model.Office;
import com.company.hardware_management_system.office.service.OfficeService;
import com.company.hardware_management_system.security.AuthenticationMetadata;
import com.company.hardware_management_system.web.dto.AddDepartmentRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/departments")
@Slf4j
public class DepartmentController {

    private final DepartmentService departmentService;
    private final DepartmentRepository departmentRepository;
    private final OfficeService officeService;

    public DepartmentController(DepartmentService departmentService, DepartmentRepository departmentRepository, OfficeService officeService) {
        this.departmentService = departmentService;
        this.departmentRepository = departmentRepository;
        this.officeService = officeService;
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

    @GetMapping("/{departmentId}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getEditDepartmentPage(@PathVariable UUID departmentId) {

        Department department = departmentService.getDepartmentById(departmentId);
        List<Office> allOffices = officeService.getAllOffices();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("edit-department");
        modelAndView.addObject("department", department);
        modelAndView.addObject("allOffices", allOffices);
        modelAndView.addObject("editDepartmentRequest",
                AddDepartmentRequest.builder()
                        .name(department.getName())
                        .build());

        log.info("Navigating to edit department page for department ID: {}", departmentId);
        return modelAndView;
    }

    @PostMapping("/{departmentId}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView updateDepartment(
            @PathVariable UUID departmentId,
            @RequestParam(required = false) List<UUID> officeIds,
            @Valid AddDepartmentRequest addDepartmentRequest,
            BindingResult bindingResult) {

        Department department = departmentService.getDepartmentById(departmentId);

        if (bindingResult.hasErrors()) {
            List<Office> allOffices = officeService.getAllOffices();

            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("edit-department");
            modelAndView.addObject("department", department);
            modelAndView.addObject("allOffices", allOffices);
            return modelAndView;
        }

        department.setName(addDepartmentRequest.getName());

        if (officeIds != null && !officeIds.isEmpty()) {
            List<Office> offices = new ArrayList<>();
            for (UUID officeId : officeIds) {
                offices.add(officeService.getOfficeById(officeId));
            }
            department.setOffices(offices);
        } else {
            department.setOffices(Collections.emptyList());
        }

        departmentRepository.save(department);
        log.info("Successfully updated department with ID: {}", departmentId);

        return new ModelAndView("redirect:/departments");
    }
}
