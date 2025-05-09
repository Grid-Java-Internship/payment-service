package com.internship.payment_service.controller;


import com.internship.payment_service.modelDTO.TransactionDTO;
import com.internship.payment_service.response.TransactionResponse;
import com.internship.payment_service.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;



    /**
     * Handles deposit transactions.
     *
     * @param transactionDTO the transaction details for the deposit
     * @return ResponseEntity containing the transaction response
     */
    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(@Valid @RequestBody TransactionDTO transactionDTO) {

        return ResponseEntity.ok(transactionService.deposit(transactionDTO));
    }

    /**
     * Handles withdrawal transactions.
     *
     * @param transactionDTO the transaction details for the withdrawal
     * @return ResponseEntity containing the transaction response
     */
    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@Valid @RequestBody TransactionDTO transactionDTO) {

        return ResponseEntity.ok(transactionService.withdraw(transactionDTO));
    }

    @GetMapping("/confirm/{id}")
    public ResponseEntity<Void> confirmTransaction(@PathVariable("id") Long transactionId) {
        transactionService.confirm(transactionId);
        return ResponseEntity.ok().build();
    }
}
