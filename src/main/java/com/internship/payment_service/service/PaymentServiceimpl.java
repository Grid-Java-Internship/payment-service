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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceimpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final UserBalanceRepository userBalanceRepository;
    private final TransactionService transactionService;



    /**
     * Perform a payment from a user to another user.
     *
     * <p>This method will check if both the sender and the receiver exist in the database.
     * If the sender does not have enough balance, the transaction will be rejected.
     * If the sender has enough balance, the transaction will be processed and the balance will be updated.
     *
     * @param paymentDTO the payment to perform
     * @return the result of the payment
     * @throws NotFoundException if the sender or the receiver are not found
     */
    @Override
    public PaymentResponse pay(PaymentDTO paymentDTO) {

        UserBalance userSender = userBalanceRepository.findById(paymentDTO.getUserSender().getUserId()).
                orElseThrow(() -> new NotFoundException("User Sender with id: " + paymentDTO.getUserSender().getUserId() + " not found!!"));

        UserBalance userReceiver = userBalanceRepository.findById(paymentDTO.getUserReceiver().getUserId()).
                orElseThrow(() -> new NotFoundException("User Receiver with id: " + paymentDTO.getUserReceiver().getUserId() + " not found!!"));

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

    @Override
    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream().map(paymentMapper::entityToDto).toList();
    }
}
