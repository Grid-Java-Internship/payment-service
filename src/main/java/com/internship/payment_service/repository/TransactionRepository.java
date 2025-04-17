package com.internship.payment_service.repository;

import com.internship.payment_service.model.Transaction;
import com.internship.payment_service.model.UserBalance;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Transaction findByTransactionId(Long transactionId);
}
