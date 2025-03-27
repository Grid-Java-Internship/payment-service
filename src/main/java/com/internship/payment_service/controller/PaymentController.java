package com.internship.payment_service.controller;

import com.internship.payment_service.modelDTO.PaymentDTO;
import com.internship.payment_service.response.PaymentResponse;
import com.internship.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


    /**
     * Perform a payment.
     *
     * @param paymentDTO the payment details
     * @return the payment result
     */
    @PostMapping("/pay")
    public ResponseEntity<PaymentResponse> pay(@Valid @RequestBody PaymentDTO paymentDTO) {
        return ResponseEntity.ok(paymentService.pay(paymentDTO));
    }

    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAllPayments(){
        return ResponseEntity.ok().body(paymentService.getAllPayments());
    }
}
