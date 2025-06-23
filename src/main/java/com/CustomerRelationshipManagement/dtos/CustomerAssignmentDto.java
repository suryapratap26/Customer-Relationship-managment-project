// CustomerAssignmentDto.java
package com.CustomerRelationshipManagement.dtos;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CustomerAssignmentDto {
    private Long id;
    private String customerPhoneNumber;
    private String userPhoneNumber;
    private LocalDateTime assignedDate;
}
