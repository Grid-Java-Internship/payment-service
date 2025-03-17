package com.internship.payment_service.service;

import com.internship.payment_service.modelDTO.PaymentDTO;
import com.internship.payment_service.response.PaymentResponse;

public interface PaymentService {

    PaymentResponse pay(PaymentDTO paymentDTO);
}
