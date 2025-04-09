package com.internship.payment_service.modelDTO;


import com.internship.payment_service.model.Status;
import com.internship.payment_service.model.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TransactionDTO {

    private Long transactionId;

    @NotNull(message = "User cannot be null!!!")
    private UserBalanceDTO userBalance;

    @NotNull(message = "You must choose type of the transaction!!!")
    private TransactionType transactionType;

    private Status status;

    private LocalDateTime timeOfTransaction;

    @NotNull(message = "Amount of money sent cannot be null!!!")
    @PositiveOrZero(message = "Amount of money sent must be greater than or equal zero!!!")
    private Double amount;
}
