package com.company.hardware_management_system.config;

import com.company.hardware_management_system.user.model.User;
import com.company.hardware_management_system.user.model.UserRole;
import com.company.hardware_management_system.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
public class AdminUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminUserInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        Optional<User> adminUser = userRepository.findByUserRoleAndUsername(UserRole.ADMIN, "admin");

        if (adminUser.isEmpty()) {
            User user = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("123456"))
                    .email("admin@company.com")
                    .userRole(UserRole.ADMIN)
                    .isActive(true)
                    .createdOn(LocalDateTime.now())
                    .build();

            userRepository.save(user);
            log.info("Default admin user created!");
        } else {
            log.info("Admin user exist.");
        }
    }
}
