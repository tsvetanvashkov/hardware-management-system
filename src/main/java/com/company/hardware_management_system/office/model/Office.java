package com.company.hardware_management_system.office.model;

import com.company.hardware_management_system.department.model.Department;
import com.company.hardware_management_system.hardware.model.Hardware;
import com.company.hardware_management_system.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
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
    private List<Department> departments = new ArrayList<>();

    @OneToMany(mappedBy = "office", fetch = FetchType.EAGER)
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "office", fetch = FetchType.EAGER)
    private List<Hardware> hardware;

    public void setDepartments(List<Department> departments) {
        // First, remove this office from all currently assigned departments
        if (this.departments != null || !this.departments.isEmpty()) {
            for (Department department : this.departments) {
                department.getOffices().remove(this);
            }
        }

        // Then set the new departments
        this.departments = departments;

        // Add this office to all new departments
        if (departments != null) {
            for (Department department : departments) {
                if (!department.getOffices().contains(this)) {
                    department.getOffices().add(this);
                }
            }
        }
    }
}
