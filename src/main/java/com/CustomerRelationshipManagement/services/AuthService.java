package com.CustomerRelationshipManagement.services;

import com.CustomerRelationshipManagement.entities.User;

public interface AuthService {
    User register(User user);
    String login(String phoneNumber, String password);
}