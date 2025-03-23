package com.company.hardware_management_system.office.repository;

import com.company.hardware_management_system.office.model.Office;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OfficeRepository extends JpaRepository<Office, UUID> {

}
