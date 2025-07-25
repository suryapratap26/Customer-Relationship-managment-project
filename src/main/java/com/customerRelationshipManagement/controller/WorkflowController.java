package com.customerRelationshipManagement.controller;

import com.customerRelationshipManagement.entities.Customer;
import com.customerRelationshipManagement.entities.CustomerAssignment;
import com.customerRelationshipManagement.services.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/workflow")
public class WorkflowController {

    @Autowired
    private WorkflowService workflowService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/assign")
    public ResponseEntity<CustomerAssignment> assignCustomerToUser(
            @RequestParam String customerPhoneNumber,
            @RequestParam String userPhoneNumber) {
        return new ResponseEntity<>(
                workflowService.assignCustomerToUser(customerPhoneNumber, userPhoneNumber), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userPhoneNumber}/customers")
    public ResponseEntity<List<Customer>> getCustomersAssignedToUser(
            @PathVariable String userPhoneNumber) {
        return new ResponseEntity<>(workflowService.getCustomersAssignedToUser(userPhoneNumber), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/customers/download")
    public ResponseEntity<ByteArrayResource> downloadAllCustomersCsv() {
        byte[] csvData = workflowService.downloadAllCustomersCsv();
        ByteArrayResource resource = new ByteArrayResource(csvData);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=customers.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/download")
    public ResponseEntity<ByteArrayResource> downloadAllUsersCsv() {
        byte[] csvData = workflowService.downloadAllUsersCsv();
        ByteArrayResource resource = new ByteArrayResource(csvData);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/predict-customers")
    public ResponseEntity<Long> predictNewCustomers(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return new ResponseEntity<>(
                workflowService.predictNewCustomers(startDate, endDate), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/merge")
    public ResponseEntity<Customer> mergeCustomers(
            @RequestParam String primaryPhoneNumber,
            @RequestParam String secondaryPhoneNumber) {
        return new ResponseEntity<>(
                workflowService.mergeCustomers(primaryPhoneNumber, secondaryPhoneNumber), HttpStatus.OK);
    }
}
