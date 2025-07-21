package com.CustomerRelationshipManagement.repositories;

import com.CustomerRelationshipManagement.entities.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    List<Customer> findByFirstNameContainingIgnoreCase(String firstName);
    List<Customer> findByLastNameContainingIgnoreCase(String lastName);
    List<Customer> findByEmailContainingIgnoreCase(String email);
    List<Customer> findByAddressContainingIgnoreCase(String address);
    long countByCreatedDateAfter(LocalDateTime date);
    Page<Customer> findAll(Pageable pageable);
}