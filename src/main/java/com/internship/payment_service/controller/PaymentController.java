package com.internship.payment_service.controller;

import com.internship.payment_service.modelDTO.PaymentDTO;
import com.internship.payment_service.response.PaymentResponse;
import com.internship.payment_service.service.PaymentService;
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
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/pay")
    public ResponseEntity<PaymentResponse> pay(@Valid @RequestBody PaymentDTO paymentDTO) {
        return ResponseEntity.ok(paymentService.pay(paymentDTO));
    }
}
