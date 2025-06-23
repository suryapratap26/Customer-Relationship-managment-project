// UserService.java
package com.CustomerRelationshipManagement.service;

import java.util.List;
import org.springframework.data.domain.Page;
import com.CustomerRelationshipManagement.dtos.UserDto;
import com.CustomerRelationshipManagement.dtos.UserTypeDto;

public interface UserService {
    UserDto createUser(UserDto userDto);
    Page<UserDto> getUsers(int page, int size, String sortBy, String keyword);
    UserDto getUser(String phoneNumber);
    UserDto updateUser(String phoneNumber, UserDto userDto);
    boolean deleteUser(String phoneNumber);
    void resetPassword(String phoneNumber);
    List<String> getAuditLogs(String phoneNumber);
    UserTypeDto createUserType(UserTypeDto userTypeDto);
    List<UserTypeDto> getAllUserTypes();
}
