package com.company.hardware_management_system.department.repository;

import com.company.hardware_management_system.department.model.Department;
import com.company.hardware_management_system.office.model.Office;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {

    Optional<Department> findByName(String name);

    Optional<List<Department>> findAllByOffices(List<Office> offices);
}
