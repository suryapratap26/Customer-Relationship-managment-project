package com.CustomerRelationshipManagement.services;

import com.CustomerRelationshipManagement.dto.RegisterRequest;
import com.CustomerRelationshipManagement.entities.User;

public interface AuthService {
    User register(RegisterRequest registerRequest);
    String login(String phoneNumber, String password);
}