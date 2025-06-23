// CustomerController.java
package com.CustomerRelationshipManagement.controller;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

import com.CustomerRelationshipManagement.dtos.CustomerDto;
import com.CustomerRelationshipManagement.service.CustomerService;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public CustomerDto createCustomer(@RequestBody CustomerDto customerDto) {
        return customerService.createCustomer(customerDto);
    }

    @GetMapping
    public Page<CustomerDto> getCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(required = false) String keyword  // if your service supports searching
    ) {
        // If your service signature is getCustomers(page, size, sortBy),
        // drop the keyword param here.
        return customerService.getCustomers(page, size, sortBy, keyword);
    }

    @GetMapping("/{phoneNumber}")
    public CustomerDto getCustomer(@PathVariable String phoneNumber) {
        return customerService.getCustomer(phoneNumber);
    }

    @PutMapping("/{phoneNumber}")
    public CustomerDto updateCustomer(
            @PathVariable String phoneNumber,
            @RequestBody CustomerDto dto) {
        return customerService.updateCustomer(phoneNumber, dto);
    }

    @PutMapping("/bulk")
    public List<CustomerDto> bulkUpdate(@RequestBody List<CustomerDto> dtos) {
        return customerService.bulkUpdate(dtos);
    }

    @DeleteMapping("/{phoneNumber}")
    public boolean deleteCustomer(@PathVariable String phoneNumber) {
        return customerService.deleteCustomer(phoneNumber);
    }

    @PostMapping("/upload")
    public String uploadCsv(@RequestParam("file") MultipartFile file) {
        customerService.uploadCsv(file);
        return "CSV file processed successfully.";
    }

    @GetMapping("/audit/{phoneNumber}")
    public List<String> getAuditLogs(@PathVariable String phoneNumber) {
        return customerService.getAuditLogs(phoneNumber);
    }
}
