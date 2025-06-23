// UserController.java
package com.CustomerRelationshipManagement.controller;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.CustomerRelationshipManagement.dtos.UserDto;
import com.CustomerRelationshipManagement.dtos.UserTypeDto;
import com.CustomerRelationshipManagement.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @GetMapping
    public Page<UserDto> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(required = false) String keyword  // if supported
    ) {
        return userService.getUsers(page, size, sortBy, keyword);
    }

    @GetMapping("/{phoneNumber}")
    public UserDto getUser(@PathVariable String phoneNumber) {
        return userService.getUser(phoneNumber);
    }

    @PutMapping("/{phoneNumber}")
    public UserDto updateUser(@PathVariable String phoneNumber, @RequestBody UserDto userDto) {
        return userService.updateUser(phoneNumber, userDto);
    }

    @DeleteMapping("/{phoneNumber}")
    public boolean deleteUser(@PathVariable String phoneNumber) {
        return userService.deleteUser(phoneNumber);
    }

    @PostMapping("/reset-password/{phoneNumber}")
    public void resetPassword(@PathVariable String phoneNumber) {
        userService.resetPassword(phoneNumber);
    }

    @GetMapping("/audit/{phoneNumber}")
    public List<String> getUserAuditLogs(@PathVariable String phoneNumber) {
        return userService.getAuditLogs(phoneNumber);
    }

    @PostMapping("/usertypes")
    public UserTypeDto createUserType(@RequestBody UserTypeDto userTypeDto) {
        return userService.createUserType(userTypeDto);
    }

    @GetMapping("/usertypes")
    public List<UserTypeDto> getAllUserTypes() {
        return userService.getAllUserTypes();
    }
}
