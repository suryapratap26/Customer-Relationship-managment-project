package com.customerRelationshipManagement.controller;


import com.customerRelationshipManagement.entities.AuditLog;
import com.customerRelationshipManagement.entities.Customer;
import com.customerRelationshipManagement.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // Create a new customer
    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        Customer createdCustomer = customerService.createCustomer(customer);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    // Retrieve paginated list of customers
    @GetMapping
    public ResponseEntity<Page<Customer>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "phoneNumber") String sortBy) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Customer> customers = customerService.getAllCustomers(pageRequest);
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    // Retrieve customer by phone number
    @GetMapping("/{phoneNumber}")
    public ResponseEntity<Customer> getCustomerByPhoneNumber(@PathVariable String phoneNumber) {
        Customer customer = customerService.getCustomerByPhoneNumber(phoneNumber);
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }

    // Search customers by any field
    @GetMapping("/search")
    public ResponseEntity<List<Customer>> searchCustomers(
            @RequestParam String field,
            @RequestParam String value) {
        List<Customer> customers = customerService.searchCustomers(field, value);
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    // Update customer information
    @PutMapping("/{phoneNumber}")
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable String phoneNumber,
            @RequestBody Customer customer) {
        Customer updatedCustomer = customerService.updateCustomer(phoneNumber, customer);
        return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
    }

    // Bulk update customers
    @PutMapping("/bulk")
    public ResponseEntity<List<Customer>> bulkUpdateCustomers(@RequestBody List<Customer> customers) {
        List<Customer> updatedCustomers = customerService.bulkUpdateCustomers(customers);
        return new ResponseEntity<>(updatedCustomers, HttpStatus.OK);
    }

    // Disable or delete customer
    @DeleteMapping("/{phoneNumber}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String phoneNumber) {
        customerService.deleteCustomer(phoneNumber);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Retrieve audit logs for a customer
    @GetMapping("/{phoneNumber}/audit")
    public ResponseEntity<List<AuditLog>> getCustomerAuditLogs(@PathVariable String phoneNumber) {
        List<AuditLog> auditLogs = customerService.getCustomerAuditLogs(phoneNumber);
        return new ResponseEntity<>(auditLogs, HttpStatus.OK);
    }

    // Bulk create customers from CSV
    @PostMapping("/bulk-upload")
    public ResponseEntity<List<Customer>> bulkCreateCustomers(@RequestParam("file") MultipartFile file) {
        List<Customer> customers = customerService.bulkCreateCustomersFromCsv(file);
        return new ResponseEntity<>(customers, HttpStatus.CREATED);
    }
}