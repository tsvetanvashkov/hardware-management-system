package com.company.hardware_management_system.department.service;

import com.company.hardware_management_system.department.model.Department;
import com.company.hardware_management_system.department.repository.DepartmentRepository;
import com.company.hardware_management_system.exception.DomainException;
import com.company.hardware_management_system.office.model.Office;
import com.company.hardware_management_system.web.dto.AddDepartmentRequest;
import com.company.hardware_management_system.web.dto.AddOfficeRequest;
import jakarta.persistence.Cacheable;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public Department addDepartment(@Valid AddDepartmentRequest addDepartmentRequest) {

        Optional<Department> optionalDepartment = departmentRepository.findByName(addDepartmentRequest.getName());
        if (optionalDepartment.isPresent()) {
            throw new DomainException("Department [%s] already exist.".formatted(addDepartmentRequest.getName()));
        }

        Department department = departmentRepository.save(initializeDepartment(addDepartmentRequest));

        log.info("Successfully create new department with name [%s]".formatted(department.getName()));

        return department;
    }

    private Department initializeDepartment(AddDepartmentRequest addDepartmentRequest) {

        return Department.builder()
                .name(addDepartmentRequest.getName())
                .build();
    }

    public Department getDepartmentById(UUID departmentId) {

        return departmentRepository.findById(departmentId).orElseThrow(() -> new DomainException("Office with id %s do not exist.".formatted(departmentId)));
    }

    public List<Department> getAllDepartments() {

        return departmentRepository.findAll();
    }

    public List<Department> getAllDepartmentsByOffices(List<Office> offices) {

        return departmentRepository.findAllByOffices(offices).orElseThrow(() -> new DomainException("Offices does not exist.".formatted(offices.isEmpty())));
    }

}
