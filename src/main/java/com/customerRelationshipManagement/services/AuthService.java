package com.customerRelationshipManagement.services;

import com.customerRelationshipManagement.entities.User;

public interface AuthService {
    User register(User user);
    String login(String phoneNumber, String password);
}