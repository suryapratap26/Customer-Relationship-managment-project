// CustomerService.java
package com.CustomerRelationshipManagement.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import com.CustomerRelationshipManagement.dtos.CustomerDto;

public interface CustomerService {
    CustomerDto createCustomer(CustomerDto dto);
    // Include 'keyword' if you support search
    Page<CustomerDto> getCustomers(int page, int size, String sortBy, String keyword);
    CustomerDto getCustomer(String phoneNumber);
    CustomerDto updateCustomer(String phoneNumber, CustomerDto dto);
    List<CustomerDto> bulkUpdate(List<CustomerDto> dtos);
    boolean deleteCustomer(String phoneNumber);
    void uploadCsv(MultipartFile file);
    List<String> getAuditLogs(String phoneNumber);
}
