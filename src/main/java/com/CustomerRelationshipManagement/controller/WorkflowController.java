// WorkflowController.java
package com.CustomerRelationshipManagement.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.CustomerRelationshipManagement.dtos.CustomerDto;
import com.CustomerRelationshipManagement.service.WorkflowService;

@RestController
@RequestMapping("/api/workflow")
public class WorkflowController {

    private final WorkflowService workflowService;

    @Autowired
    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping("/assign")
    public String assignCustomerToUser(@RequestParam String customerPhone, @RequestParam String userPhone) {
        workflowService.assignCustomer(customerPhone, userPhone);
        return "Customer assigned successfully.";
    }

    @GetMapping("/assigned-customers/{userPhone}")
    public List<CustomerDto> getCustomersAssignedToUser(@PathVariable String userPhone) {
        return workflowService.getCustomersByUser(userPhone);
    }

    @GetMapping("/export/customers")
    public byte[] exportCustomerCsv() {
        return workflowService.downloadCustomerCsv();
    }

    @GetMapping("/export/users")
    public byte[] exportUserCsv() {
        return workflowService.downloadUserCsv();
    }

    @GetMapping("/predict-customers")
    public Integer predictCustomerGrowth(@RequestParam String from, @RequestParam String to) {
        return workflowService.predictNewCustomers(from, to);
    }

    @PostMapping("/merge")
    public CustomerDto mergeCustomers(@RequestParam String primaryPhone, @RequestParam String secondaryPhone) {
        return workflowService.mergeCustomers(primaryPhone, secondaryPhone);
    }
}
