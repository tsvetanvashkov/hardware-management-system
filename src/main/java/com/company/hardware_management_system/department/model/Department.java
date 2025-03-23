package com.company.hardware_management_system.department.model;

import com.company.hardware_management_system.hardware.model.Hardware;
import com.company.hardware_management_system.office.model.Office;
import com.company.hardware_management_system.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "departments", fetch = FetchType.EAGER)
    private List<Office> offices;

    @OneToMany(mappedBy = "department", fetch = FetchType.EAGER)
    private List<User> users;

    @OneToMany(mappedBy = "department", fetch = FetchType.EAGER)
    private List<Hardware> hardware;
}
