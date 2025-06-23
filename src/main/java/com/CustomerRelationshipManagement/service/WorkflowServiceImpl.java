// WorkflowServiceImpl.java
package com.CustomerRelationshipManagement.service;

import com.CustomerRelationshipManagement.dtos.CustomerDto;
import com.CustomerRelationshipManagement.entities.Customer;
import com.CustomerRelationshipManagement.entities.CustomerAssignment;
import com.CustomerRelationshipManagement.entities.User;
import com.CustomerRelationshipManagement.repository.CustomerAssignmentRepository;
import com.CustomerRelationshipManagement.repository.CustomerRepository;
import com.CustomerRelationshipManagement.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class WorkflowServiceImpl implements WorkflowService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final CustomerAssignmentRepository assignmentRepository;

    @Autowired
    public WorkflowServiceImpl(CustomerRepository customerRepository,
                               UserRepository userRepository,
                               CustomerAssignmentRepository assignmentRepository) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
    }

    @Override
    @Transactional
    public void assignCustomer(String customerPhone, String userPhone) {
        Customer customer = customerRepository.findById(customerPhone)
                .orElseThrow(() -> new NoSuchElementException("Customer not found"));
        User user = userRepository.findById(userPhone)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        CustomerAssignment assignment = new CustomerAssignment();
        assignment.setCustomer(customer);
        assignment.setUser(user);
        assignment.setAssignedDate(LocalDateTime.now());
        assignmentRepository.save(assignment);
    }

    @Override
    public List<CustomerDto> getCustomersByUser(String userPhone) {
        User user = userRepository.findById(userPhone)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        return assignmentRepository.findByUser(user).stream()
                .map(CustomerAssignment::getCustomer)
                .map(entity -> {
                    CustomerDto dto = new CustomerDto();
                    dto.setPhoneNumber(entity.getPhoneNumber());
                    dto.setFirstName(entity.getFirstName());
                    dto.setLastName(entity.getLastName());
                    dto.setEmail(entity.getEmail());
                    dto.setAddress(entity.getAddress());
                    dto.setActive(entity.isActive());
                    dto.setCreatedDate(entity.getCreatedDate());
                    dto.setLastModifiedDate(entity.getLastModifiedDate());
                    dto.setCreatedBy(entity.getCreatedBy());
                    dto.setLastModifiedBy(entity.getLastModifiedBy());
                    return dto;
                }).collect(Collectors.toList());
    }

    @Override
    public byte[] downloadCustomerCsv() {
        List<Customer> customers = customerRepository.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("phoneNumber,firstName,lastName,email,address,active\n");
        for (Customer c : customers) {
            sb.append(c.getPhoneNumber()).append(",")
              .append(c.getFirstName()).append(",")
              .append(c.getLastName()).append(",")
              .append(c.getEmail() != null ? c.getEmail() : "").append(",")
              .append(c.getAddress() != null ? c.getAddress() : "").append(",")
              .append(c.isActive()).append("\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] downloadUserCsv() {
        List<User> users = userRepository.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("phoneNumber,firstName,lastName,email,userType,active\n");
        for (User u : users) {
            sb.append(u.getPhoneNumber()).append(",")
              .append(u.getFirstName()).append(",")
              .append(u.getLastName()).append(",")
              .append(u.getEmail() != null ? u.getEmail() : "").append(",")
              .append(u.getUserType().getTypeName()).append(",")
              .append(u.isActive()).append("\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Integer predictNewCustomers(String from, String to) {
        // Stub: return 0 or implement logic
        return 0;
    }

    @Override
    @Transactional
    public CustomerDto mergeCustomers(String primaryPhone, String secondaryPhone) {
        Customer primary = customerRepository.findById(primaryPhone)
                .orElseThrow(() -> new NoSuchElementException("Primary customer not found"));
        Customer secondary = customerRepository.findById(secondaryPhone)
                .orElseThrow(() -> new NoSuchElementException("Secondary customer not found"));
        // Merge non-null fields if primary missing
        if (primary.getFirstName() == null && secondary.getFirstName() != null) {
            primary.setFirstName(secondary.getFirstName());
        }
        if (primary.getLastName() == null && secondary.getLastName() != null) {
            primary.setLastName(secondary.getLastName());
        }
        if (primary.getEmail() == null && secondary.getEmail() != null) {
            primary.setEmail(secondary.getEmail());
        }
        if (primary.getAddress() == null && secondary.getAddress() != null) {
            primary.setAddress(secondary.getAddress());
        }
        // Delete secondary
        customerRepository.delete(secondary);
        Customer saved = customerRepository.save(primary);
        CustomerDto dto = new CustomerDto();
        dto.setPhoneNumber(saved.getPhoneNumber());
        dto.setFirstName(saved.getFirstName());
        dto.setLastName(saved.getLastName());
        dto.setEmail(saved.getEmail());
        dto.setAddress(saved.getAddress());
        dto.setActive(saved.isActive());
        dto.setCreatedDate(saved.getCreatedDate());
        dto.setLastModifiedDate(saved.getLastModifiedDate());
        dto.setCreatedBy(saved.getCreatedBy());
        dto.setLastModifiedBy(saved.getLastModifiedBy());
        return dto;
    }
}
