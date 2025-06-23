package com.CustomerRelationshipManagement.entities;



import lombok.Data;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;


//User Entity
@Entity
@Table(name = "users")
@Data
@EntityListeners(AuditingEntityListener.class)
public class User {
 @Id
 @Column(name = "phone_number", length = 15, unique = true)
 private String phoneNumber;

 @Column(nullable = false)
 private String firstName;

 @Column(nullable = false)
 private String lastName;

 @Column
 private String email;

 @Column(nullable = false)
 private String password;

 @ManyToOne
 @JoinColumn(name = "user_type_id", nullable = false)
 private UserType userType;

 @Column(nullable = false)
 private boolean active = true;

 @CreatedDate
 @Column(updatable = false)
 private LocalDateTime createdDate;

 @LastModifiedDate
 private LocalDateTime lastModifiedDate;

 @CreatedBy
 @Column(updatable = false)
 private String createdBy;

 @LastModifiedBy
 private String lastModifiedBy;
}
