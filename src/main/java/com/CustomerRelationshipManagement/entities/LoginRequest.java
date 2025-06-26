package com.CustomerRelationshipManagement.entities;

import lombok.Data;

@Data
public class LoginRequest {
    private String phoneNumber;
    private String password;

}
