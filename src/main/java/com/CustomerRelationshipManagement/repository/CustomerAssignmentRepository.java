package com.CustomerRelationshipManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CustomerRelationshipManagement.entities.Customer;
import com.CustomerRelationshipManagement.entities.CustomerAssignment;
import com.CustomerRelationshipManagement.entities.User;
import java.util.List;

@Repository
public interface CustomerAssignmentRepository extends JpaRepository<CustomerAssignment, Long> {
    List<CustomerAssignment> findByUser(User user);
    List<CustomerAssignment> findByCustomer(Customer customer);
}

