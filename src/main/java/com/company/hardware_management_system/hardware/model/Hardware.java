package com.company.hardware_management_system.hardware.model;

import com.company.hardware_management_system.department.model.Department;
import com.company.hardware_management_system.office.model.Office;
import com.company.hardware_management_system.project.model.Project;
import com.company.hardware_management_system.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Hardware {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true,nullable = false)
    private String officeNumber;

    @Column(nullable = false)
    private String category;

    @Column(unique = true)
    private String serialNumber;

    private String model;

    private String Brand;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Condition itemCondition;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    private LocalDate purchasedOn;

    private LocalDate warrantyExpiring;

    private LocalDate lastMaintenance;

    @Column(nullable = false)
    private LocalDateTime inStatus;

    @ManyToOne
    private Office office;

    @ManyToOne
    private Department department;

    @ManyToOne
    private Project project;

    @ManyToOne
    private User owner;

    private String notes;

}
