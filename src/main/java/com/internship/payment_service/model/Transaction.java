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
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @NotNull(message = "User cannot be null!!!")
    @ManyToOne(cascade = {CascadeType.MERGE})
    private UserBalance userBalance;

    @NotNull(message = "You must choose type of the transaction!!!")
    private TransactionType transactionType;

    @NotNull(message = "You must set the transaction status!!!")
    private Status status;

    @CreationTimestamp
    private LocalDateTime timeOfTransaction;

    @NotNull(message = "Amount of money sent cannot be null!!!")
    @PositiveOrZero(message = "Amount of money sent must be greater than or equal zero!!!")
    private Double amount;

}
