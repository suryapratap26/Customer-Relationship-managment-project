package com.CustomerRelationshipManagement.services;

import com.CustomerRelationshipManagement.entities.AuditLog;
import com.CustomerRelationshipManagement.entities.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CustomerService {
    Customer createCustomer(Customer customer);
    Page<Customer> getAllCustomers(Pageable pageable);
    Customer getCustomerByPhoneNumber(String phoneNumber);
    List<Customer> searchCustomers(String field, String value);
    Customer updateCustomer(String phoneNumber, Customer customer);
    List<Customer> bulkUpdateCustomers(List<Customer> customers);
    void deleteCustomer(String phoneNumber);
    List<AuditLog> getCustomerAuditLogs(String phoneNumber);
    List<Customer> bulkCreateCustomersFromCsv(MultipartFile file);
}