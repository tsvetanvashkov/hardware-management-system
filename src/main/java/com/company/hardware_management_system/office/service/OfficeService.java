package com.company.hardware_management_system.office.service;

import com.company.hardware_management_system.exception.DomainException;
import com.company.hardware_management_system.office.model.Office;
import com.company.hardware_management_system.office.repository.OfficeRepository;
import com.company.hardware_management_system.web.dto.AddOfficeRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class OfficeService {

    private final OfficeRepository officeRepository;

    public OfficeService(OfficeRepository officeRepository) {
        this.officeRepository = officeRepository;
    }

    public Office addOffice(AddOfficeRequest addOfficeRequest) {

        Optional<Office> optionalOffice = officeRepository.findByNameAndLocation(addOfficeRequest.getName(), addOfficeRequest.getLocation());
        if (optionalOffice.isPresent()) {
            throw new DomainException("Office [%s] on location [%s] already exist.".formatted(addOfficeRequest.getName(), addOfficeRequest.getLocation()));
        }

        Office office = officeRepository.save(initializeOffice(addOfficeRequest));

        log.info("Successfully create new office with name [%s] and location [%s]".formatted(office.getName(), office.getLocation()));

        return office;
    }

    private Office initializeOffice(AddOfficeRequest addOfficeRequest) {

        return Office.builder()
                .name(addOfficeRequest.getName())
                .location(addOfficeRequest.getLocation())
                .build();
    }

    public List<Office> getAllOffices() {

        return officeRepository.findAll();
    }

    public Office getOfficeById(UUID officeId) {

        return officeRepository.findById(officeId).orElseThrow(() -> new DomainException("Office with id [%s] does not exist.".formatted(officeId)));
    }

}
