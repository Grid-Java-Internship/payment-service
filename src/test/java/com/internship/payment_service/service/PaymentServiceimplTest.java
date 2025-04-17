package com.internship.payment_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.internship.payment_service.exception.NotFoundException;
import com.internship.payment_service.mapper.PaymentMapper;
import com.internship.payment_service.model.Payment;
import com.internship.payment_service.model.Status;
import com.internship.payment_service.model.UserBalance;
import com.internship.payment_service.modelDTO.PaymentDTO;
import com.internship.payment_service.modelDTO.UserBalanceDTO;
import com.internship.payment_service.repository.PaymentRepository;
import com.internship.payment_service.repository.UserBalanceRepository;
import com.internship.payment_service.response.PaymentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private UserBalanceRepository userBalanceRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private PaymentServiceimpl paymentService;

    private PaymentDTO paymentDTO;
    private UserBalance userSender;
    private UserBalance userReceiver;
    private Payment payment;
    UserBalanceDTO userSenderDTO;
    UserBalanceDTO userReceiverDTO;
    private final LocalDateTime currentDateTime = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        userSenderDTO = UserBalanceDTO.builder()
                .userId(1L)
                .balance(5000.0)
                .build();
        userReceiverDTO = UserBalanceDTO.builder()
                .userId(2L)
                .balance(1000.0)
                .build();
        paymentDTO = PaymentDTO.builder()
                .userSender(userSenderDTO)
                .userReceiver(userReceiverDTO)
                .amount(1000.0)
                .timeOfPayment(currentDateTime)
                .build();
        userSender = UserBalance.builder()
                .userId(1L)
                .balance(5000.0)
                .build();
        userReceiver = UserBalance.builder()
                .userId(2L)
                .balance(1000.0)
                .build();
        payment = Payment.builder()
                .amount(1000.0)
                .timeOfPayment(currentDateTime)
                .build();
    }


    @Test
    void pay_shouldTransferMoneySuccessfully_whenFundsAreSufficient() {

        when(userBalanceRepository.findById(userSender.getUserId())).thenReturn(Optional.of(userSender));
        when(userBalanceRepository.findById(userReceiver.getUserId())).thenReturn(Optional.of(userReceiver));
        when(transactionService.processStatusType(paymentDTO.getAmount(), userSender.getBalance())).thenReturn(Status.COMPLETED);
        when(paymentMapper.dtoToEntity(paymentDTO)).thenReturn(payment);
        when(paymentRepository.save(payment)).thenReturn(payment);

        PaymentResponse response = paymentService.pay(paymentDTO);

        assertNotNull(response);
        assertEquals("You have successfully transferred 1000.0 credits to user with id: 2. New balance: 4000.0", response.getReturnMessage());
        assertEquals(payment.getUserSender(),userSender);
        assertEquals(payment.getUserReceiver(),userReceiver);
        assertEquals(4000.0,payment.getUserSender().getBalance());
        assertEquals(2000.0,payment.getUserReceiver().getBalance());

        verify(userBalanceRepository).findById(userSender.getUserId());
        verify(userBalanceRepository).findById(userReceiver.getUserId());
        verify(paymentMapper).dtoToEntity(paymentDTO);
        verify(transactionService).processStatusType(paymentDTO.getAmount(), userSenderDTO.getBalance());
        verify(paymentRepository).save(payment);
    }

    @Test
    void pay_shouldRejectTransaction_whenInsufficientFunds() {

        userSender.setBalance(100.0);

        when(userBalanceRepository.findById(1L)).thenReturn(Optional.of(userSender));
        when(userBalanceRepository.findById(2L)).thenReturn(Optional.of(userReceiver));
        System.out.println(paymentDTO.getUserSender().getBalance());
        when(transactionService.processStatusType(paymentDTO.getAmount(),userSender.getBalance())).thenReturn(Status.REJECTED);
        when(paymentMapper.dtoToEntity(paymentDTO)).thenReturn(payment);
        when(paymentRepository.save(payment)).thenReturn(payment);

        PaymentResponse response = paymentService.pay(paymentDTO);

        assertNotNull(response);
        assertEquals("Transaction rejected. Insufficient funds in your account.", response.getReturnMessage());
        assertEquals(payment.getUserSender(),userSender);
        assertEquals(payment.getUserReceiver(),userReceiver);
        assertEquals(100,payment.getUserSender().getBalance());
        assertEquals(1000,payment.getUserReceiver().getBalance());

        verify(userBalanceRepository).findById(userSender.getUserId());
        verify(userBalanceRepository).findById(userReceiver.getUserId());
        verify(paymentMapper).dtoToEntity(paymentDTO);
        verify(transactionService).processStatusType(paymentDTO.getAmount(),userSender.getBalance());
        verify(paymentRepository).save(payment);
    }

    @Test
    void pay_shouldThrowException_whenSenderAndReceiverAreSame() {

        when(userBalanceRepository.findById(paymentDTO.getUserSender().getUserId())).thenReturn(Optional.of(userSender));
        when(userBalanceRepository.findById(paymentDTO.getUserReceiver().getUserId())).thenReturn(Optional.of(userSender));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> paymentService.pay(paymentDTO));
        assertEquals("You cannot transfer money to yourself", exception.getMessage());

        verify(userBalanceRepository).findById(userSender.getUserId());
        verify(userBalanceRepository).findById(userReceiver.getUserId());
        verify(transactionService, never()).processStatusType(paymentDTO.getAmount(),userSender.getBalance());
        verify(paymentMapper, never()).dtoToEntity(paymentDTO);
        verify(paymentRepository, never()).save(payment);

    }

    @Test
    void pay_shouldThrowException_whenSenderNotFound() {
        when(userBalanceRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> paymentService.pay(paymentDTO));
        assertEquals("User with id: 1 not found!!", exception.getMessage());
    }

    @Test
    void pay_shouldThrowException_whenReceiverNotFound() {
        when(userBalanceRepository.findById(1L)).thenReturn(Optional.of(userSender));
        when(userBalanceRepository.findById(2L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> paymentService.pay(paymentDTO));
        assertEquals("User with id: 2 not found!!", exception.getMessage());
    }

}
