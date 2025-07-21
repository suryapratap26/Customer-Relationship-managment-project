package com.CustomerRelationshipManagement.services;

import com.CustomerRelationshipManagement.entities.Customer;
import com.CustomerRelationshipManagement.entities.CustomerAssignment;
import com.CustomerRelationshipManagement.entities.User;
import com.CustomerRelationshipManagement.repositories.CustomerAssignmentRepository;
import com.CustomerRelationshipManagement.repositories.CustomerRepository;
import com.CustomerRelationshipManagement.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkflowServiceImpl implements WorkflowService {

    @Autowired
    private CustomerAssignmentRepository customerAssignmentRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public CustomerAssignment assignCustomerToUser(String customerPhoneNumber, String userPhoneNumber) {
        Customer customer = customerRepository.findById(customerPhoneNumber)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        User user = userRepository.findById(userPhoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CustomerAssignment assignment = new CustomerAssignment();
        assignment.setCustomer(customer);
        assignment.setUser(user);
        assignment.setAssignedDate(LocalDateTime.now());
        return customerAssignmentRepository.save(assignment);
    }

    @Override
    public List<Customer> getCustomersAssignedToUser(String userPhoneNumber) {
        return customerAssignmentRepository.findByUserPhoneNumber(userPhoneNumber)
                .stream()
                .map(CustomerAssignment::getCustomer)
                .collect(Collectors.toList());
    }

    @Override
    public byte[] downloadAllCustomersCsv() {
        List<Customer> customers = customerRepository.findAll();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(baos)) {
            writer.println("PhoneNumber,FirstName,LastName,Email,Address,Active,CreatedDate,LastModifiedDate");
            for (Customer customer : customers) {
                writer.println(String.format("%s,%s,%s,%s,%s,%b,%s,%s",
                        customer.getPhoneNumber(),
                        customer.getFirstName(),
                        customer.getLastName(),
                        customer.getEmail(),
                        customer.getAddress(),
                        customer.isActive(),
                        customer.getCreatedDate(),
                        customer.getLastModifiedDate()));
            }
        }
        return baos.toByteArray();
    }

    @Override
    public byte[] downloadAllUsersCsv() {
        List<User> users = userRepository.findAll();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(baos)) {
            writer.println("PhoneNumber,FirstName,LastName,Email,UserType,Active,CreatedDate,LastModifiedDate");
            for (User user : users) {
                writer.println(String.format("%s,%s,%s,%s,%s,%b,%s,%s",
                        user.getPhoneNumber(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getUserType().name(),
                        user.isActive(),
                        user.getCreatedDate(),
                        user.getLastModifiedDate()));
            }
        }
        return baos.toByteArray();
    }

    @Override
    public long predictNewCustomers(LocalDate startDate, LocalDate endDate) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMonthAgo = now.minusMonths(1);
        long customerCount = customerRepository.countByCreatedDateAfter(oneMonthAgo);
        long daysInRange = startDate.until(endDate.plusDays(1), java.time.temporal.ChronoUnit.DAYS);
        long daysInMonth = 30; // Approximation
        return Math.round((customerCount / (double) daysInMonth) * daysInRange);
    }

    @Override
    @Transactional
    public Customer mergeCustomers(String primaryPhoneNumber, String secondaryPhoneNumber) {
        Customer primary = customerRepository.findById(primaryPhoneNumber)
                .orElseThrow(() -> new RuntimeException("Primary customer not found"));
        Customer secondary = customerRepository.findById(secondaryPhoneNumber)
                .orElseThrow(() -> new RuntimeException("Secondary customer not found"));

        if (primary.getEmail() == null && secondary.getEmail() != null) {
            primary.setEmail(secondary.getEmail());
        }
        if (primary.getAddress() == null && secondary.getAddress() != null) {
            primary.setAddress(secondary.getAddress());
        }
        primary.setLastModifiedDate(LocalDateTime.now());
        primary.setLastModifiedBy(getCurrentUser());

        secondary.setActive(false);
        customerRepository.save(secondary);

        return customerRepository.save(primary);
    }

    private String getCurrentUser() {
        // Placeholder for retrieving current authenticated user
        return "system";
    }
}