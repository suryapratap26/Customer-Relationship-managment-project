package com.CustomerRelationshipManagement.services;

import com.CustomerRelationshipManagement.entities.AuditLog;
import com.CustomerRelationshipManagement.entities.User;
import com.CustomerRelationshipManagement.entities.UserType;
import com.CustomerRelationshipManagement.repositories.AuditLogRepository;
import com.CustomerRelationshipManagement.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User createUser(User user) {
        validateUser(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedDate(LocalDateTime.now());
        user.setLastModifiedDate(LocalDateTime.now());
        user.setCreatedBy(getCurrentUser());
        user.setLastModifiedBy(getCurrentUser());
        return userRepository.save(user);
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findById(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public List<User> searchUsers(String field, String value) {
        switch (field.toLowerCase()) {
            case "firstname":
                return userRepository.findByFirstNameContainingIgnoreCase(value);
            case "lastname":
                return userRepository.findByLastNameContainingIgnoreCase(value);
            case "email":
                return userRepository.findByEmailContainingIgnoreCase(value);
            default:
                throw new IllegalArgumentException("Invalid search field");
        }
    }

    @Override
    @Transactional
    public User updateUser(String phoneNumber, User updatedUser) {
        User existingUser = getUserByPhoneNumber(phoneNumber);
        logChanges(existingUser, updatedUser);
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setUserType(updatedUser.getUserType());
        existingUser.setLastModifiedDate(LocalDateTime.now());
        existingUser.setLastModifiedBy(getCurrentUser());
        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(String phoneNumber) {
        User user = getUserByPhoneNumber(phoneNumber);
        user.setActive(false);
        user.setLastModifiedDate(LocalDateTime.now());
        user.setLastModifiedBy(getCurrentUser());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void resetPassword(String phoneNumber, String newPassword) {
        User user = getUserByPhoneNumber(phoneNumber);
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setLastModifiedDate(LocalDateTime.now());
        user.setLastModifiedBy(getCurrentUser());
        userRepository.save(user);
        saveAuditLog("User", phoneNumber, "password", "****", "****");
    }

    @Override
    public List<AuditLog> getUserAuditLogs(String phoneNumber) {
        return auditLogRepository.findByEntityTypeAndEntityId("User", phoneNumber);
    }

    private void validateUser(User user) {
        if (user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) {
            throw new IllegalArgumentException("Phone number is required");
        }
        if (user.getFirstName() == null || user.getFirstName().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (user.getLastName() == null || user.getLastName().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (user.getUserType() == null) {
            throw new IllegalArgumentException("User type is required");
        }
    }

    private void logChanges(User oldUser, User newUser) {
        if (!oldUser.getFirstName().equals(newUser.getFirstName())) {
            saveAuditLog("User", oldUser.getPhoneNumber(), "firstName",
                    oldUser.getFirstName(), newUser.getFirstName());
        }
        if (!oldUser.getLastName().equals(newUser.getLastName())) {
            saveAuditLog("User", oldUser.getPhoneNumber(), "lastName",
                    oldUser.getLastName(), newUser.getLastName());
        }
        if (!oldUser.getEmail().equals(newUser.getEmail())) {
            saveAuditLog("User", oldUser.getPhoneNumber(), "email",
                    oldUser.getEmail(), newUser.getEmail());
        }
        if (oldUser.getUserType() != newUser.getUserType()) {
            saveAuditLog("User", oldUser.getPhoneNumber(), "userType",
                    oldUser.getUserType().name(), newUser.getUserType().name());
        }
    }

    private void saveAuditLog(String entityType, String entityId, String fieldName, String oldValue, String newValue) {
        AuditLog log = new AuditLog();
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setFieldName(fieldName);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        log.setChangeDate(LocalDateTime.now());
        log.setChangedBy(getCurrentUser());
        auditLogRepository.save(log);
    }

    private String getCurrentUser() {
        // Placeholder for retrieving current authenticated user
        return "system";
    }
}