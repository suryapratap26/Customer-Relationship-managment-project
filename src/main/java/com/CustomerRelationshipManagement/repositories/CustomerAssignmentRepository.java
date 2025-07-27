package com.customerRelationshipManagement.repositories;

import com.customerRelationshipManagement.entities.CustomerAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerAssignmentRepository extends JpaRepository<CustomerAssignment, Long> {
    List<CustomerAssignment> findByUserPhoneNumber(String userPhoneNumber);
}