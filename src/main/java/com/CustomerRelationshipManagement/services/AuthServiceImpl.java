package com.CustomerRelationshipManagement.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CustomerRelationshipManagement.dto.RegisterRequest;
import com.CustomerRelationshipManagement.entities.User;
import com.CustomerRelationshipManagement.entities.UserType;
import com.CustomerRelationshipManagement.repositories.UserRepository;
import com.CustomerRelationshipManagement.utils.JwtUtil;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @Transactional
    public User register(RegisterRequest registerRequest) {
        if (userRepository.existsById(registerRequest.getPhoneNumber())) {
            throw new IllegalArgumentException("User already exists with phone number: " + registerRequest.getPhoneNumber());
        }
        User user = new User();
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setUserType(UserType.USER); // Set default user type to USER
        user.setCreatedDate(LocalDateTime.now());
        user.setLastModifiedDate(LocalDateTime.now());
        user.setCreatedBy(getCurrentUser());
        user.setLastModifiedBy(getCurrentUser());
        return userRepository.save(user);
    }

    @Override
    public String login(String phoneNumber, String password) {
        User user = userRepository.findById(phoneNumber)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }
        return jwtUtil.generateToken(phoneNumber);
    }

    private String getCurrentUser() {
        // Placeholder for retrieving current authenticated user
        return "system";
    }
}