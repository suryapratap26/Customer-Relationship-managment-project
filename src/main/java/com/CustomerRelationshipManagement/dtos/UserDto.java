// UserDto.java
package com.CustomerRelationshipManagement.dtos;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserDto {
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String userType;
    private boolean active;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private String createdBy;
    private String lastModifiedBy;
}
