package com.customerRelationshipManagement.services;

import com.customerRelationshipManagement.entities.Customer;
import com.customerRelationshipManagement.entities.CustomerAssignment;

import java.time.LocalDate;
import java.util.List;

public interface WorkflowService {
    CustomerAssignment assignCustomerToUser(String customerPhoneNumber, String userPhoneNumber);
    List<Customer> getCustomersAssignedToUser(String userPhoneNumber);
    byte[] downloadAllCustomersCsv();
    byte[] downloadAllUsersCsv();
    long predictNewCustomers(LocalDate startDate, LocalDate endDate);
    Customer mergeCustomers(String primaryPhoneNumber, String secondaryPhoneNumber);
}