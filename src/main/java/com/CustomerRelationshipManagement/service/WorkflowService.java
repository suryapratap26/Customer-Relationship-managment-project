// WorkflowService.java
package com.CustomerRelationshipManagement.service;

import java.util.List;
import org.springframework.data.domain.Page; // if needed
import com.CustomerRelationshipManagement.dtos.CustomerDto;

public interface WorkflowService {
    void assignCustomer(String customerPhone, String userPhone);
    List<CustomerDto> getCustomersByUser(String userPhone);
    byte[] downloadCustomerCsv();
    byte[] downloadUserCsv();
    Integer predictNewCustomers(String from, String to);
    CustomerDto mergeCustomers(String primaryPhone, String secondaryPhone);
}
