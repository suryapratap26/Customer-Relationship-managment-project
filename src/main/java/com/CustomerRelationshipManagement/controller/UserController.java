package com.customerRelationshipManagement.controller;

import com.customerRelationshipManagement.dto.PasswordResetRequest;
import com.customerRelationshipManagement.entities.AuditLog;
import com.customerRelationshipManagement.entities.User;
import com.customerRelationshipManagement.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Admin-only: create users
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
    }

    // Admin-only: view all users
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<User>> getAllUsers(Pageable pageable) {
        return new ResponseEntity<>(userService.getAllUsers(pageable), HttpStatus.OK);
    }

    // Self or Admin: get user
    @PreAuthorize("#phoneNumber == authentication.name or hasRole('ADMIN')")
    @GetMapping("/{phoneNumber}")
    public ResponseEntity<User> getUserByPhoneNumber(@PathVariable String phoneNumber) {
        return new ResponseEntity<>(userService.getUserByPhoneNumber(phoneNumber), HttpStatus.OK);
    }

    // Admin-only: search users
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String field, @RequestParam String value) {
        return new ResponseEntity<>(userService.searchUsers(field, value), HttpStatus.OK);
    }

    // Admin-only: update users
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{phoneNumber}")
    public ResponseEntity<User> updateUser(@PathVariable String phoneNumber, @RequestBody User user) {
        return new ResponseEntity<>(userService.updateUser(phoneNumber, user), HttpStatus.OK);
    }

    // Admin-only: delete users
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{phoneNumber}")
    public ResponseEntity<Void> deleteUser(@PathVariable String phoneNumber) {
        userService.deleteUser(phoneNumber);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Self or Admin: reset password
    @PreAuthorize("#phoneNumber == authentication.name or hasRole('ADMIN')")
    @PutMapping("/{phoneNumber}/reset-password")
    public ResponseEntity<Void> resetPassword(@PathVariable String phoneNumber,
                                              @RequestBody PasswordResetRequest request) {
        userService.resetPassword(phoneNumber, request.getNewPassword());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Admin-only: audit logs
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{phoneNumber}/audit-logs")
    public ResponseEntity<List<AuditLog>> getUserAuditLogs(@PathVariable String phoneNumber) {
        return new ResponseEntity<>(userService.getUserAuditLogs(phoneNumber), HttpStatus.OK);
    }
}

