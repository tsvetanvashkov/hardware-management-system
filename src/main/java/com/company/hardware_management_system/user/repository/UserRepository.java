package com.company.hardware_management_system.user.repository;

import com.company.hardware_management_system.user.model.User;
import com.company.hardware_management_system.user.model.UserRole;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUserRoleAndUsername(UserRole userRole, String username);

    Optional<User> findByUsername(String username);

}
