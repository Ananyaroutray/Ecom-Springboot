package com.java.ecom.entity;

import com.java.ecom.enums.ReturnStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Return {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private String userId;

    @Enumerated(EnumType.STRING)
    private ReturnStatus returnStatus;

    private String reason;

    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
}
