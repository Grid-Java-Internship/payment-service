package com.internship.payment_service.service;

import com.internship.payment_service.modelDTO.PaymentDTO;
import com.internship.payment_service.response.PaymentResponse;

import java.util.List;

public interface PaymentService {

    PaymentResponse pay(PaymentDTO paymentDTO);

    List<PaymentDTO> getAllPayments();
}
