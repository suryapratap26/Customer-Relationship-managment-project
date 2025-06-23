// BulkCustomerDto.java
package com.CustomerRelationshipManagement.dtos;

import lombok.Data;

@Data
public class BulkCustomerDto {
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
}
