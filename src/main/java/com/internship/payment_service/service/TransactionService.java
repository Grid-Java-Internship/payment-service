package com.internship.payment_service.service;


import com.internship.payment_service.model.Status;
import com.internship.payment_service.modelDTO.TransactionDTO;
import com.internship.payment_service.response.TransactionResponse;

public interface TransactionService {

    TransactionResponse deposit(TransactionDTO transactionDTO);

    TransactionResponse withdraw(TransactionDTO transactionDTO);

    Status processStatusType(Double amount);

    Status processStatusType(Double amountToWithdraw, Double userBalance);

}
