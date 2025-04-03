package com.company.hardware_management_system.department.model;

import com.company.hardware_management_system.hardware.model.Hardware;
import com.company.hardware_management_system.office.model.Office;
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
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "departments", fetch = FetchType.EAGER)
    private List<Office> offices = new ArrayList<>();

    @OneToMany(mappedBy = "department", fetch = FetchType.EAGER)
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "department", fetch = FetchType.EAGER)
    private List<Hardware> hardware;

    public void setOffices(List<Office> offices) {
        // First, remove this department from all currently assigned offices
        if (this.offices != null || !this.offices.isEmpty()) {
            for (Office office : this.offices) {
                office.getDepartments().remove(this);
            }
        }

        // Then set the new offices
        this.offices = offices;

        // Add this department to all new offices
        if (offices != null) {
            for (Office office : offices) {
                if (!office.getDepartments().contains(this)) {
                    office.getDepartments().add(this);
                }
            }
        }
    }
}
