package com.company.hardware_management_system.project.model;

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
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToMany(mappedBy = "projects", fetch = FetchType.EAGER)
    private List<User> users;

    @OneToMany(mappedBy = "project", fetch = FetchType.EAGER)
    private List<Hardware> hardware;
}
