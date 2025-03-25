package com.company.hardware_management_system.web;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/offices")
@Slf4j
public class OfficeController {

    private final OfficeService officeService;
    private final OfficeRepository officeRepository;

    public OfficeController(OfficeService officeService, OfficeRepository officeRepository) {
        this.officeService = officeService;
        this.officeRepository = officeRepository;
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
}
