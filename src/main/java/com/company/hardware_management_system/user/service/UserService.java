package com.company.hardware_management_system.user.service;

import com.company.hardware_management_system.exception.ConfirmPasswordException;
import com.company.hardware_management_system.exception.DomainException;
import com.company.hardware_management_system.office.model.Office;
import com.company.hardware_management_system.security.AuthenticationMetadata;
import com.company.hardware_management_system.user.model.User;
import com.company.hardware_management_system.user.repository.UserRepository;
import com.company.hardware_management_system.web.dto.AddUserRequest;
import com.company.hardware_management_system.web.dto.ChangePasswordRequest;
import com.company.hardware_management_system.web.dto.EditProfileRequest;
import jakarta.validation.Valid;
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

    public User addUser(AddUserRequest addUserRequest, Office office) {

        Optional<User> optionUser = userRepository.findByUsername(addUserRequest.getUsername());
        if (optionUser.isPresent()) {
            throw new DomainException("Username [%s] already exist.".formatted(addUserRequest.getUsername()));
        }

        User user = userRepository.save(initializeUser(addUserRequest, office));

        log.info("Successfully create new user account for username [%s] and id [%s]".formatted(user.getUsername(), user.getId()));

        return user;
    }

    private User initializeUser(AddUserRequest addUserRequest, Office office) {

        return User.builder()
                .username(addUserRequest.getUsername())
                .password(passwordEncoder.encode(addUserRequest.getPassword()))
                .email(addUserRequest.getEmail())
                .userRole(addUserRequest.getUserRole())
                .isActive(true)
                .office(office)
                .department(addUserRequest.getDepartment())
                .createdOn(LocalDateTime.now())
                .build();
    }

    public User getById(UUID id) {

        return userRepository.findById(id).orElseThrow(() -> new DomainException("User with id [%s] does not exist.".formatted(id)));
    }

    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    public String getEmailBodyForCreatedUser(AddUserRequest addUserRequest) {
        StringBuilder body = new StringBuilder();
        body.append("Your account in Hardware Management System was created!");
        body.append(System.lineSeparator());
        body.append(("You can log in from link: "));
        body.append("http://localhost:8080/login");
        body.append(System.lineSeparator());
        body.append("Username: %s".formatted(addUserRequest.getUsername()));
        body.append(System.lineSeparator());
        body.append("Password: %s".formatted(addUserRequest.getPassword()));

        return body.toString();
    }

    public void editUserDetails(User user, EditProfileRequest editProfileRequest) {

        user.setFirstName(editProfileRequest.getFirstName());
        user.setLastName(editProfileRequest.getLastName());
        user.setProfilePicture(editProfileRequest.getProfilePicture());

        userRepository.save(user);
    }

    public void changeUserPassword(User user, @Valid ChangePasswordRequest changePasswordRequest) {

        if (!changePasswordRequest.isPasswordConfirmed()) {
            throw new ConfirmPasswordException("Passwords do not match");
        }
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
    }
}
