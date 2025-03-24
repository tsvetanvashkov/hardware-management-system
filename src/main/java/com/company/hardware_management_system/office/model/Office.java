package com.company.hardware_management_system.office.model;

import com.company.hardware_management_system.department.model.Department;
import com.company.hardware_management_system.hardware.model.Hardware;
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
public class Office {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "office_department")
    private List<Department> departments;

    @OneToMany(mappedBy = "office", fetch = FetchType.EAGER)
    private List<User> users;

    @OneToMany(mappedBy = "office", fetch = FetchType.EAGER)
    private List<Hardware> hardware;

}
