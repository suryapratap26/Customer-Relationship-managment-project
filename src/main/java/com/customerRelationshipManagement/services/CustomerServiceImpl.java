package com.customerRelationshipManagement.services;

import com.customerRelationshipManagement.entities.AuditLog;
import com.customerRelationshipManagement.entities.Customer;
import com.customerRelationshipManagement.repositories.AuditLogRepository;
import com.customerRelationshipManagement.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public Customer createCustomer(Customer customer) {

        customer.setCreatedDate(LocalDateTime.now());
        customer.setLastModifiedDate(LocalDateTime.now());
        customer.setCreatedBy(getCurrentUser());
        customer.setLastModifiedBy(getCurrentUser());
        return customerRepository.save(customer);
    }

    @Override
    public Page<Customer> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    @Override
    public Customer getCustomerByPhoneNumber(String phoneNumber) {
        return customerRepository.findById(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    @Override
    public List<Customer> searchCustomers(String field, String value) {
        switch (field.toLowerCase()) {
            case "firstname":
                return customerRepository.findByFirstNameContainingIgnoreCase(value);
            case "lastname":
                return customerRepository.findByLastNameContainingIgnoreCase(value);
            case "email":
                return customerRepository.findByEmailContainingIgnoreCase(value);
            case "address":
                return customerRepository.findByAddressContainingIgnoreCase(value);
            default:
                throw new IllegalArgumentException("Invalid search field");
        }
    }

    @Override
    @Transactional
    public Customer updateCustomer(String phoneNumber, Customer updatedCustomer) {
        Customer existingCustomer = getCustomerByPhoneNumber(phoneNumber);
        logChanges(existingCustomer, updatedCustomer);
        existingCustomer.setFirstName(updatedCustomer.getFirstName());
        existingCustomer.setLastName(updatedCustomer.getLastName());
        existingCustomer.setEmail(updatedCustomer.getEmail());
        existingCustomer.setAddress(updatedCustomer.getAddress());
        existingCustomer.setLastModifiedDate(LocalDateTime.now());
        existingCustomer.setLastModifiedBy(getCurrentUser());
        return customerRepository.save(existingCustomer);
    }

    @Override
    @Transactional
    public List<Customer> bulkUpdateCustomers(List<Customer> customers) {
        List<Customer> updatedCustomers = new ArrayList<>();
        for (Customer customer : customers) {
            updatedCustomers.add(updateCustomer(customer.getPhoneNumber(), customer));
        }
        return updatedCustomers;
    }

    @Override
    @Transactional
    public void deleteCustomer(String phoneNumber) {
        Customer customer = getCustomerByPhoneNumber(phoneNumber);
        customer.setActive(false);
        customer.setLastModifiedDate(LocalDateTime.now());
        customer.setLastModifiedBy(getCurrentUser());
        customerRepository.save(customer);
    }

    @Override
    public List<AuditLog> getCustomerAuditLogs(String phoneNumber) {
        return auditLogRepository.findByEntityTypeAndEntityId("Customer", phoneNumber);
    }

    @Override
    @Transactional
    public List<Customer> bulkCreateCustomersFromCsv(MultipartFile file) {
        List<Customer> customers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 4) {
                    Customer customer = new Customer();
                    customer.setPhoneNumber(data[0].trim());
                    customer.setFirstName(data[1].trim());
                    customer.setLastName(data[2].trim());
                    customer.setEmail(data[3].trim());
                    customer.setAddress(data.length > 4 ? data[4].trim() : "");
                    validateCustomer(customer);
                    customers.add(customer);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error processing CSV file: " + e.getMessage());
        }
        return customerRepository.saveAll(customers);
    }

    private void validateCustomer(Customer customer) {
        if (customer.getPhoneNumber() == null || customer.getPhoneNumber().isEmpty()) {
            throw new IllegalArgumentException("Phone number is required");
        }
        if (customer.getFirstName() == null || customer.getFirstName().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (customer.getLastName() == null || customer.getLastName().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
    }

    private void logChanges(Customer oldCustomer, Customer newCustomer) {
        if (!oldCustomer.getFirstName().equals(newCustomer.getFirstName())) {
            saveAuditLog("Customer", oldCustomer.getPhoneNumber(), "firstName",
                    oldCustomer.getFirstName(), newCustomer.getFirstName());
        }
        if (!oldCustomer.getLastName().equals(newCustomer.getLastName())) {
            saveAuditLog("Customer", oldCustomer.getPhoneNumber(), "lastName",
                    oldCustomer.getLastName(), newCustomer.getLastName());
        }
        if (!oldCustomer.getEmail().equals(newCustomer.getEmail())) {
            saveAuditLog("Customer", oldCustomer.getPhoneNumber(), "email",
                    oldCustomer.getEmail(), newCustomer.getEmail());
        }
        if (!oldCustomer.getAddress().equals(newCustomer.getAddress())) {
            saveAuditLog("Customer", oldCustomer.getPhoneNumber(), "address",
                    oldCustomer.getAddress(), newCustomer.getAddress());
        }
    }

    private void saveAuditLog(String entityType, String entityId, String fieldName, String oldValue, String newValue) {
        AuditLog log = new AuditLog();
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setFieldName(fieldName);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        log.setChangeDate(LocalDateTime.now());
        log.setChangedBy(getCurrentUser());
        auditLogRepository.save(log);
    }

    private String getCurrentUser() {
        
        return "system";
    }
}