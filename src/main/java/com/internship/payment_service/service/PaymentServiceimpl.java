package com.internship.payment_service.service;

import com.internship.payment_service.exception.NotFoundException;
import com.internship.payment_service.mapper.PaymentMapper;
import com.internship.payment_service.model.Payment;
import com.internship.payment_service.model.Status;
import com.internship.payment_service.model.UserBalance;
import com.internship.payment_service.modelDTO.PaymentDTO;
import com.internship.payment_service.repository.PaymentRepository;
import com.internship.payment_service.repository.UserBalanceRepository;
import com.internship.payment_service.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceimpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final UserBalanceRepository userBalanceRepository;
    private final TransactionService transactionService;


    @Override
    public PaymentResponse pay(PaymentDTO paymentDTO) {

        UserBalance userSender = userBalanceRepository.findById(paymentDTO.getUserSender().getUserId()).
                orElseThrow(() -> new NotFoundException("User with id: " + paymentDTO.getUserSender().getUserId() + " not found!!"));

        UserBalance userReceiver = userBalanceRepository.findById(paymentDTO.getUserReceiver().getUserId()).
                orElseThrow(() -> new NotFoundException("User with id: " + paymentDTO.getUserReceiver().getUserId() + " not found!!"));

        if(Objects.equals(userSender.getUserId(), userReceiver.getUserId())) {
            throw new IllegalArgumentException("You cannot transfer money to yourself");
        }

        Status status = transactionService.processStatusType(paymentDTO.getAmount(), userSender.getBalance());
        paymentDTO.setStatus(status);
        log.info("{}", paymentDTO);
        if (paymentDTO.getStatus() == Status.COMPLETED) {
            userSender.setBalance(userSender.getBalance() - paymentDTO.getAmount());
            userReceiver.setBalance(userReceiver.getBalance() + paymentDTO.getAmount());
        }

        Payment payment = paymentMapper.dtoToEntity(paymentDTO);
        payment.setUserSender(userSender);
        payment.setUserReceiver(userReceiver);

        log.info("{}", payment);

        payment = paymentRepository.save(payment);

        return PaymentResponse.builder()
                .returnMessage(paymentDTO.getStatus() == Status.COMPLETED
                        ? "You have successfully transferred " + paymentDTO.getAmount() + " credits to user with id: " + payment.getUserReceiver().getUserId() + ". New balance: " + payment.getUserSender().getBalance()
                        : "Transaction rejected. Insufficient funds in your account.")
                .build();


    }
}
