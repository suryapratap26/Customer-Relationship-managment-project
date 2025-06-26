package com.CustomerRelationshipManagement.controller;


import com.CustomerRelationshipManagement.entities.Customer;
import com.CustomerRelationshipManagement.entities.CustomerAssignment;
import com.CustomerRelationshipManagement.services.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/workflow")
public class WorkflowController {

    @Autowired
    private WorkflowService workflowService;

    // Assign a customer to a user
    @PostMapping("/assign")
    public ResponseEntity<CustomerAssignment> assignCustomerToUser(
            @RequestParam String customerPhoneNumber,
            @RequestParam String userPhoneNumber) {
        CustomerAssignment assignment = workflowService.assignCustomerToUser(customerPhoneNumber, userPhoneNumber);
        return new ResponseEntity<>(assignment, HttpStatus.CREATED);
    }

    // Retrieve customers assigned to a user
    @GetMapping("/user/{userPhoneNumber}/customers")
    public ResponseEntity<List<Customer>> getCustomersAssignedToUser(@PathVariable String userPhoneNumber) {
        List<Customer> customers = workflowService.getCustomersAssignedToUser(userPhoneNumber);
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    // Download all customer data as CSV
    @GetMapping("/customers/download")
    public ResponseEntity<ByteArrayResource> downloadAllCustomersCsv() {
        byte[] csvData = workflowService.downloadAllCustomersCsv();
        ByteArrayResource resource = new ByteArrayResource(csvData);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=customers.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

    // Download all user data as CSV
    @GetMapping("/users/download")
    public ResponseEntity<ByteArrayResource> downloadAllUsersCsv() {
        byte[] csvData = workflowService.downloadAllUsersCsv();
        ByteArrayResource resource = new ByteArrayResource(csvData);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

    // Predict expected number of new customers
    @GetMapping("/predict-customers")
    public ResponseEntity<Long> predictNewCustomers(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        long predictedCount = workflowService.predictNewCustomers(startDate, endDate);
        return new ResponseEntity<>(predictedCount, HttpStatus.OK);
    }

    // Merge two customer records
    @PostMapping("/merge")
    public ResponseEntity<Customer> mergeCustomers(
            @RequestParam String primaryPhoneNumber,
            @RequestParam String secondaryPhoneNumber) {
        Customer mergedCustomer = workflowService.mergeCustomers(primaryPhoneNumber, secondaryPhoneNumber);
        return new ResponseEntity<>(mergedCustomer, HttpStatus.OK);
    }
}