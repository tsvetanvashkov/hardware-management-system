package com.company.hardware_management_system.user.model;

import com.company.hardware_management_system.department.model.Department;
import com.company.hardware_management_system.hardware.model.Hardware;
import com.company.hardware_management_system.office.model.Office;
import com.company.hardware_management_system.project.model.Project;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    @Email
    private String email;

    private String firstName;

    private String lastName;

    @URL
    private String profilePicture;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    private boolean isActive;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    @ManyToOne
    private Office office;

    @ManyToOne
    private Department department;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_project")
    private List<Project> projects;

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private List<Hardware> hardware;

}
