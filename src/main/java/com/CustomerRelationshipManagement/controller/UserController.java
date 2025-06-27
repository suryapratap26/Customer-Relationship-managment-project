package com.CustomerRelationshipManagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.CustomerRelationshipManagement.dto.PasswordResetRequest;
import com.CustomerRelationshipManagement.entities.AuditLog;
import com.CustomerRelationshipManagement.entities.User;
import com.CustomerRelationshipManagement.services.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<User>> getAllUsers(Pageable pageable) {
        Page<User> users = userService.getAllUsers(pageable);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{phoneNumber}")
    public ResponseEntity<User> getUserByPhoneNumber(@PathVariable String phoneNumber) {
        User user = userService.getUserByPhoneNumber(phoneNumber);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String field, @RequestParam String value) {
        List<User> users = userService.searchUsers(field, value);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/{phoneNumber}")
    public ResponseEntity<User> updateUser(@PathVariable String phoneNumber, @RequestBody User user) {
        User updatedUser = userService.updateUser(phoneNumber, user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping("/{phoneNumber}")
    public ResponseEntity<Void> deleteUser(@PathVariable String phoneNumber) {
        userService.deleteUser(phoneNumber);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{phoneNumber}/reset-password")
    public ResponseEntity<Void> resetPassword(@PathVariable String phoneNumber, @RequestBody PasswordResetRequest request) {
        userService.resetPassword(phoneNumber, request.getNewPassword());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{phoneNumber}/audit-logs")
    public ResponseEntity<List<AuditLog>> getUserAuditLogs(@PathVariable String phoneNumber) {
        List<AuditLog> auditLogs = userService.getUserAuditLogs(phoneNumber);
        return new ResponseEntity<>(auditLogs, HttpStatus.OK);
    }
}
