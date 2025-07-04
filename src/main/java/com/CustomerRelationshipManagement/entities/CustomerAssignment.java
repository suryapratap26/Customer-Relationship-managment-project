package com.CustomerRelationshipManagement.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "customer_assignments")
@Data
public class CustomerAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_phone_number", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "user_phone_number", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime assignedDate;
}

