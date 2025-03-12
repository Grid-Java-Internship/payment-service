package com.internship.payment_service.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User cannot be null!!!")
    @ManyToOne
    private UserBalance userSender;

    @NotNull(message = "User cannot be null!!!")
    @ManyToOne
    private UserBalance userReceiver;

    @NotNull(message = "Amount of money sent cannot be null!!!")
    @PositiveOrZero(message = "Amount of money sent must be greater than or equal zero!!!")
    private Double amount;

    @CreationTimestamp
    private LocalDateTime timeOfPayment;

}
