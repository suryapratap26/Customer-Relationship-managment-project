package com.CustomerRelationshipManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CustomerRelationshipManagement.entities.UserType;

@Repository
public interface UserTypeRepository extends JpaRepository<UserType, Long> {
}