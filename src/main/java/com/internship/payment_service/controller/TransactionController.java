package com.internship.payment_service.controller;


import com.internship.payment_service.modelDTO.TransactionDTO;
import com.internship.payment_service.response.TransactionResponse;
import com.internship.payment_service.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(@Valid @RequestBody TransactionDTO transactionDTO) {

        return ResponseEntity.ok(transactionService.deposit(transactionDTO));
    }

    }
