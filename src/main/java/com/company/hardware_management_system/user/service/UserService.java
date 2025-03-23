package com.company.hardware_management_system.user.service;

import com.company.hardware_management_system.exception.DomainException;
import com.company.hardware_management_system.security.AuthenticationMetadata;
import com.company.hardware_management_system.user.model.User;
import com.company.hardware_management_system.user.model.UserRole;
import com.company.hardware_management_system.user.repository.UserRepository;
import com.company.hardware_management_system.web.dto.AddUserRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username).orElseThrow(() -> new DomainException("User with this username does not exist."));

        return new AuthenticationMetadata(user.getId(), username, user.getPassword(), user.getUserRole(), user.isActive());
    }

    public User addUser(AddUserRequest addUserRequest) {

        Optional<User> optionUser = userRepository.findByUsername(addUserRequest.getUsername());
        if (optionUser.isPresent()) {
            throw new DomainException("Username [%s] already exist.".formatted(addUserRequest.getUsername()));
        }

        User user = userRepository.save(initializeUser(addUserRequest));

        log.info("Successfully create new user account for username [%s] and id [%s]".formatted(user.getUsername(), user.getId()));

        return user;
    }

    private User initializeUser(AddUserRequest addUserRequest) {

        return User.builder()
                .username(addUserRequest.getUsername())
                .password(passwordEncoder.encode(addUserRequest.getPassword()))
                .email(addUserRequest.getEmail())
                .userRole(addUserRequest.getUserRole())
                .isActive(true)
                .office(addUserRequest.getOffice())
                .department(addUserRequest.getDepartment())
                .createdOn(LocalDateTime.now())
                .build();
    }

    public User getById(UUID id) {

        return userRepository.findById(id).orElseThrow(() -> new DomainException("User with id [%s] does not exist.".formatted(id)));
    }

    //@Cacheable("users")
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }
}
