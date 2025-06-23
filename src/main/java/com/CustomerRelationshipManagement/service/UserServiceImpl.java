// UserServiceImpl.java
package com.CustomerRelationshipManagement.service;

import com.CustomerRelationshipManagement.dtos.UserDto;
import com.CustomerRelationshipManagement.dtos.UserTypeDto;
import com.CustomerRelationshipManagement.entities.AuditLog;
import com.CustomerRelationshipManagement.entities.User;
import com.CustomerRelationshipManagement.entities.UserType;
import com.CustomerRelationshipManagement.repository.AuditLogRepository;
import com.CustomerRelationshipManagement.repository.UserRepository;
import com.CustomerRelationshipManagement.repository.UserTypeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;    // correct import
import org.springframework.data.domain.Sort;        // correct import
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserTypeRepository userTypeRepository;
    private final AuditLogRepository auditLogRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           UserTypeRepository userTypeRepository,
                           AuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.userTypeRepository = userTypeRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User entity = new User();
        entity.setPhoneNumber(userDto.getPhoneNumber());
        entity.setFirstName(userDto.getFirstName());
        entity.setLastName(userDto.getLastName());
        entity.setEmail(userDto.getEmail());
        entity.setPassword(userDto.getPassword());
        UserType type = userTypeRepository.findById(Long.parseLong(userDto.getUserType()))
                .orElseThrow(() -> new NoSuchElementException("UserType not found"));
        entity.setUserType(type);
        entity.setActive(true);
        User saved = userRepository.save(entity);
        saveAudit(saved, null, null, "Created");
        return toDto(saved);
    }

    @Override
    public Page<UserDto> getUsers(int page, int size, String sortBy, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<User> pageEntity;
        if (keyword != null && !keyword.isBlank()) {
            pageEntity = userRepository.searchUsers(keyword, pageable);
        } else {
            pageEntity = userRepository.findAll(pageable);
        }
        return pageEntity.map(this::toDto);
    }

    @Override
    public UserDto getUser(String phoneNumber) {
        User entity = userRepository.findById(phoneNumber)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        return toDto(entity);
    }

    @Override
    @Transactional
    public UserDto updateUser(String phoneNumber, UserDto userDto) {
        User existing = userRepository.findById(phoneNumber)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        if (!Objects.equals(existing.getFirstName(), userDto.getFirstName())) {
            saveAudit(existing, "firstName", existing.getFirstName(), userDto.getFirstName());
            existing.setFirstName(userDto.getFirstName());
        }
        if (!Objects.equals(existing.getLastName(), userDto.getLastName())) {
            saveAudit(existing, "lastName", existing.getLastName(), userDto.getLastName());
            existing.setLastName(userDto.getLastName());
        }
        if (!Objects.equals(existing.getEmail(), userDto.getEmail())) {
            saveAudit(existing, "email", existing.getEmail(), userDto.getEmail());
            existing.setEmail(userDto.getEmail());
        }
        if (userDto.getPassword() != null) {
            saveAudit(existing, "password", "[PROTECTED]", "[PROTECTED]");
            existing.setPassword(userDto.getPassword());
        }
        if (!Objects.equals(existing.getUserType().getId().toString(), userDto.getUserType())) {
            UserType newType = userTypeRepository.findById(Long.parseLong(userDto.getUserType()))
                    .orElseThrow(() -> new NoSuchElementException("UserType not found"));
            saveAudit(existing, "userType", existing.getUserType().getTypeName(), newType.getTypeName());
            existing.setUserType(newType);
        }
        if (existing.isActive() != userDto.isActive()) {
            saveAudit(existing, "active",
                      String.valueOf(existing.isActive()),
                      String.valueOf(userDto.isActive()));
            existing.setActive(userDto.isActive());
        }
        User updated = userRepository.save(existing);
        return toDto(updated);
    }

    @Override
    @Transactional
    public boolean deleteUser(String phoneNumber) {
        return userRepository.findById(phoneNumber).map(entity -> {
            userRepository.delete(entity);
            saveAudit(entity, null, "Exists", "Deleted");
            return true;
        }).orElse(false);
    }

    @Override
    @Transactional
    public void resetPassword(String phoneNumber) {
        User existing = userRepository.findById(phoneNumber)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        String newPass = UUID.randomUUID().toString();
        saveAudit(existing, "password", "[PROTECTED]", "[PROTECTED]");
        existing.setPassword(newPass);
        userRepository.save(existing);
        // In real app: send newPass via email/SMS
    }

    @Override
    public List<String> getAuditLogs(String phoneNumber) {
        List<AuditLog> logs = auditLogRepository
                .findByEntityTypeAndEntityIdOrderByChangeDateDesc("User", phoneNumber);
        return logs.stream()
                .map(log -> log.getChangeDate() + " by " + log.getChangedBy() + ": " +
                        (log.getFieldName() == null
                         ? log.getNewValue()
                         : log.getFieldName() + " changed to " + log.getNewValue()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserTypeDto createUserType(UserTypeDto userTypeDto) {
        UserType entity = new UserType();
        entity.setTypeName(userTypeDto.getTypeName());
        UserType saved = userTypeRepository.save(entity);
        UserTypeDto dto = new UserTypeDto();
        dto.setId(saved.getId());
        dto.setTypeName(saved.getTypeName());
        return dto;
    }

    @Override
    public List<UserTypeDto> getAllUserTypes() {
        return userTypeRepository.findAll().stream()
                .map(type -> {
                    UserTypeDto dto = new UserTypeDto();
                    dto.setId(type.getId());
                    dto.setTypeName(type.getTypeName());
                    return dto;
                }).collect(Collectors.toList());
    }

    private void saveAudit(User entity, String fieldName, String oldValue, String newValue) {
        AuditLog log = new AuditLog();
        log.setEntityType("User");
        log.setEntityId(entity.getPhoneNumber());
        log.setFieldName(fieldName);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        log.setChangeDate(LocalDateTime.now());
        log.setChangedBy("SYSTEM");
        auditLogRepository.save(log);
    }

    private UserDto toDto(User entity) {
        UserDto dto = new UserDto();
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setUserType(entity.getUserType().getId().toString());
        dto.setActive(entity.isActive());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setLastModifiedDate(entity.getLastModifiedDate());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setLastModifiedBy(entity.getLastModifiedBy());
        return dto;
    }
}
