package com.company.hardware_management_system.project.model;

import com.company.hardware_management_system.hardware.model.Hardware;
import com.company.hardware_management_system.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
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

    public void setUsers(List<User> users) {
        // First, remove this project from all currently assigned users
        if (this.users != null) {
            for (User user : this.users) {
                user.getProjects().remove(this);
            }
        }

        // Then set the new users
        this.users = users;

        // Add this project to all new users
        if (users != null) {
            for (User user : users) {
                if (!user.getProjects().contains(this)) {
                    user.getProjects().add(this);
                }
            }
        }
    }
}
