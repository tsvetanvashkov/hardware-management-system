package com.company.hardware_management_system.hardware.repository;

import com.company.hardware_management_system.hardware.model.Hardware;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HardwareRepository extends JpaRepository<Hardware, UUID> {

}
