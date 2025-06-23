// CustomerServiceImpl.java
package com.CustomerRelationshipManagement.service;

import com.CustomerRelationshipManagement.dtos.CustomerDto;
import com.CustomerRelationshipManagement.entities.AuditLog;
import com.CustomerRelationshipManagement.entities.Customer;
import com.CustomerRelationshipManagement.repository.AuditLogRepository;
import com.CustomerRelationshipManagement.repository.CustomerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;           // correct import
import org.springframework.data.domain.Sort;               // correct import
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;              // or org.springframework.transaction.annotation.Transactional
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final AuditLogRepository auditLogRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository,
                               AuditLogRepository auditLogRepository) {
        this.customerRepository = customerRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    @Transactional
    public CustomerDto createCustomer(CustomerDto dto) {
        Customer entity = new Customer();
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.setAddress(dto.getAddress());
        entity.setActive(true);
        Customer saved = customerRepository.save(entity);
        // Audit: creation
        AuditLog log = new AuditLog();
        log.setEntityType("Customer");
        log.setEntityId(saved.getPhoneNumber());
        log.setFieldName(null);
        log.setOldValue(null);
        log.setNewValue("Created");
        log.setChangeDate(LocalDateTime.now());
        log.setChangedBy("SYSTEM");
        auditLogRepository.save(log);
        return toDto(saved);
    }

    @Override
    public Page<CustomerDto> getCustomers(int page, int size, String sortBy, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Customer> pageEntity;
        if (keyword != null && !keyword.isBlank()) {
            pageEntity = customerRepository.searchCustomers(keyword, pageable);
        } else {
            pageEntity = customerRepository.findAll(pageable);
        }
        return pageEntity.map(this::toDto);
    }

    @Override
    public CustomerDto getCustomer(String phoneNumber) {
        Customer entity = customerRepository.findById(phoneNumber)
                .orElseThrow(() -> new NoSuchElementException("Customer not found"));
        return toDto(entity);
    }

    @Override
    @Transactional
    public CustomerDto updateCustomer(String phoneNumber, CustomerDto dto) {
        Customer existing = customerRepository.findById(phoneNumber)
                .orElseThrow(() -> new NoSuchElementException("Customer not found"));
        // Compare and audit fields
        if (!Objects.equals(existing.getFirstName(), dto.getFirstName())) {
            saveAudit(existing, "firstName", existing.getFirstName(), dto.getFirstName());
            existing.setFirstName(dto.getFirstName());
        }
        if (!Objects.equals(existing.getLastName(), dto.getLastName())) {
            saveAudit(existing, "lastName", existing.getLastName(), dto.getLastName());
            existing.setLastName(dto.getLastName());
        }
        if (!Objects.equals(existing.getEmail(), dto.getEmail())) {
            saveAudit(existing, "email", existing.getEmail(), dto.getEmail());
            existing.setEmail(dto.getEmail());
        }
        if (!Objects.equals(existing.getAddress(), dto.getAddress())) {
            saveAudit(existing, "address", existing.getAddress(), dto.getAddress());
            existing.setAddress(dto.getAddress());
        }
        if (existing.isActive() != dto.isActive()) {
            saveAudit(existing, "active",
                      String.valueOf(existing.isActive()), String.valueOf(dto.isActive()));
            existing.setActive(dto.isActive());
        }
        Customer updated = customerRepository.save(existing);
        return toDto(updated);
    }

    @Override
    @Transactional
    public List<CustomerDto> bulkUpdate(List<CustomerDto> dtos) {
        List<CustomerDto> result = new ArrayList<>();
        for (CustomerDto dto : dtos) {
            CustomerDto updated = updateCustomer(dto.getPhoneNumber(), dto);
            result.add(updated);
        }
        return result;
    }

    @Override
    @Transactional
    public boolean deleteCustomer(String phoneNumber) {
        return customerRepository.findById(phoneNumber).map(entity -> {
            customerRepository.delete(entity);
            saveAudit(entity, null, "Exists", "Deleted");
            return true;
        }).orElse(false);
    }

    @Override
    @Transactional
    public void uploadCsv(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            // Assuming header present
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] cols = line.split(",");
                if (cols.length >= 4) {
                    CustomerDto dto = new CustomerDto();
                    dto.setPhoneNumber(cols[0].trim());
                    dto.setFirstName(cols[1].trim());
                    dto.setLastName(cols[2].trim());
                    dto.setEmail(cols[3].trim());
                    if (cols.length > 4) dto.setAddress(cols[4].trim());
                    try {
                        createCustomer(dto);
                    } catch (Exception e) {
                        // skip or log invalid rows
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to process CSV", e);
        }
    }

    @Override
    public List<String> getAuditLogs(String phoneNumber) {
        List<AuditLog> logs = auditLogRepository
                .findByEntityTypeAndEntityIdOrderByChangeDateDesc("Customer", phoneNumber);
        return logs.stream()
                .map(log -> log.getChangeDate() + " by " + log.getChangedBy() + ": " +
                        (log.getFieldName() == null
                         ? log.getNewValue()
                         : log.getFieldName() + " changed to " + log.getNewValue()))
                .collect(Collectors.toList());
    }

    private void saveAudit(Customer entity, String fieldName, String oldValue, String newValue) {
        AuditLog log = new AuditLog();
        log.setEntityType("Customer");
        log.setEntityId(entity.getPhoneNumber());
        log.setFieldName(fieldName);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        log.setChangeDate(LocalDateTime.now());
        log.setChangedBy("SYSTEM");
        auditLogRepository.save(log);
    }

    private CustomerDto toDto(Customer entity) {
        CustomerDto dto = new CustomerDto();
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setAddress(entity.getAddress());
        dto.setActive(entity.isActive());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setLastModifiedDate(entity.getLastModifiedDate());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setLastModifiedBy(entity.getLastModifiedBy());
        return dto;
    }
}
