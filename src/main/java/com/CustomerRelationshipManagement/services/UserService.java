package com.CustomerRelationshipManagement.services;

import com.CustomerRelationshipManagement.entities.AuditLog;
import com.CustomerRelationshipManagement.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    User createUser(User user);
    Page<User> getAllUsers(Pageable pageable);
    User getUserByPhoneNumber(String phoneNumber);
    List<User> searchUsers(String field, String value);
    User updateUser(String phoneNumber, User user);
    void deleteUser(String phoneNumber);
    void resetPassword(String phoneNumber, String newPassword);
    List<AuditLog> getUserAuditLogs(String phoneNumber);
}