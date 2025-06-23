// AuditLogDto.java
package com.CustomerRelationshipManagement.dtos;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class AuditLogDto {
    private Long id;
    private String entityType;
    private String entityId;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private LocalDateTime changeDate;
    private String changedBy;
}
