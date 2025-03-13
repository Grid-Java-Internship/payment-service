package com.internship.payment_service.modelDTO;

import com.internship.payment_service.model.Status;
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
public class PaymentDTO {


    @NotNull(message = "User who sends money cannot be null!!!")
    private UserBalanceDTO userSender;

    @NotNull(message = "User who receives money cannot be null!!!")
    private UserBalanceDTO userReceiver;

    @NotNull(message = "Amount of money sent cannot be null!!!")
    @PositiveOrZero(message = "Amount of money sent must be greater than or equal zero!!!")
    private Double amount;

    private LocalDateTime timeOfPayment;

    @NotNull(message = "You must set the payment status!!!")
    private Status status;
}
