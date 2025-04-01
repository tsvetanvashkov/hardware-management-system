package com.company.hardware_management_system.web;

import com.company.hardware_management_system.department.model.Department;
import com.company.hardware_management_system.department.service.DepartmentService;
import com.company.hardware_management_system.exception.DomainException;
import com.company.hardware_management_system.office.model.Office;
import com.company.hardware_management_system.office.repository.OfficeRepository;
import com.company.hardware_management_system.office.service.OfficeService;
import com.company.hardware_management_system.project.model.Project;
import com.company.hardware_management_system.security.AuthenticationMetadata;
import com.company.hardware_management_system.web.dto.AddOfficeRequest;
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
@RequestMapping("/offices")
@Slf4j
public class OfficeController {

    private final OfficeService officeService;
    private final OfficeRepository officeRepository;
    private final DepartmentService departmentService;

    public OfficeController(OfficeService officeService, OfficeRepository officeRepository, DepartmentService departmentService) {
        this.officeService = officeService;
        this.officeRepository = officeRepository;
        this.departmentService = departmentService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAllOffices(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        List<Office> offices = officeService.getAllOffices();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("offices");
        modelAndView.addObject("offices", offices);

        log.info("Navigating to offices page.");
        return modelAndView;
    }

    @GetMapping("/add-office")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAddOfficePage() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-office");
        modelAndView.addObject("addOfficeRequest", new AddOfficeRequest());

        log.info("Navigating to add office page.");
        return modelAndView;
    }

    @PostMapping("/add-office")
    public ModelAndView addOffice(@Valid AddOfficeRequest addOfficeRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("add-office");
        }

        Office office = officeService.addOffice(addOfficeRequest);
        officeRepository.save(office);
        log.info("Successfully added office to database.");

        return new ModelAndView("redirect:/offices");
    }

    @GetMapping("/{officeId}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getEditOfficePage(@PathVariable UUID officeId) {

        Office office = officeService.getOfficeById(officeId);
        List<Department> allDepartments = departmentService.getAllDepartments();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("edit-office");
        modelAndView.addObject("office", office);
        modelAndView.addObject("allDepartments", allDepartments);
        modelAndView.addObject("editOfficeRequest",
                AddOfficeRequest.builder()
                        .name(office.getName())
                        .location(office.getLocation())
                        .build());

        return modelAndView;
    }

    @PostMapping("/{officeId}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView updateOffice(
            @PathVariable UUID officeId,
            @Valid AddOfficeRequest addOfficeRequest,
            BindingResult bindingResult,
            @RequestParam(required = false) List<UUID> departmentIds) {

        Office office = officeService.getOfficeById(officeId);

        if (bindingResult.hasErrors()) {
            List<Department> allDepartments = departmentService.getAllDepartments();

            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("edit-office");
            modelAndView.addObject("office", office);
            modelAndView.addObject("allDepartments", allDepartments);
            return modelAndView;
        }

        // Update office details
        office.setName(addOfficeRequest.getName());
        office.setLocation(addOfficeRequest.getLocation());

        // Update departments
        if (departmentIds != null && !departmentIds.isEmpty()) {
            List<Department> departments = new ArrayList<>();
            for (UUID departmentId : departmentIds) {
                departments.add(departmentService.getDepartmentById(departmentId));
            }
            office.setDepartments(departments);
        } else {
            office.setDepartments(Collections.emptyList());
        }

        officeRepository.save(office);
        return new ModelAndView("redirect:/offices");
    }
}
