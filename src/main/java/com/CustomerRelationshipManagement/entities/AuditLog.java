package com.customerRelationshipManagement.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "audit_logs")
@Data
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String entityType; 

    @Column(nullable = false)
    private String entityId; 

    @Column
    private String fieldName;

    @Column
    private String oldValue;

    @Column
    private String newValue;

    @Column(nullable = false)
    private LocalDateTime changeDate;

    @Column(nullable = false)
    private String changedBy;
}
