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
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
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

    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

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
        try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
            paymentDTO.setUserSender(null);
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn("1");

            when(userBalanceRepository.findByUserId(1L)).thenReturn(userSender);
            when(userBalanceRepository.findById(userReceiver.getUserId())).thenReturn(Optional.of(userReceiver));
            when(transactionService.processStatusType(paymentDTO.getAmount(), userSender.getBalance())).thenReturn(Status.COMPLETED);
            when(paymentMapper.dtoToEntity(paymentDTO)).thenReturn(payment);
            when(paymentRepository.save(payment)).thenReturn(payment);

            PaymentResponse response = paymentService.pay(paymentDTO);

            assertNotNull(response);
            assertEquals("You have successfully transferred 1000.0 credits to user with id: 2. New balance: 4000.0", response.getReturnMessage());
            assertEquals(userSender, payment.getUserSender());
            assertEquals(userReceiver, payment.getUserReceiver());
            assertEquals(4000.0, userSender.getBalance());
            assertEquals(2000.0, userReceiver.getBalance());

            verify(userBalanceRepository).findByUserId(1L);
            verify(userBalanceRepository).findById(userReceiver.getUserId());
            verify(paymentMapper).dtoToEntity(paymentDTO);
            verify(transactionService).processStatusType(paymentDTO.getAmount(), userSenderDTO.getBalance());
            verify(paymentRepository).save(payment);
        }
    }

    @Test
    void pay_shouldRejectTransaction_whenInsufficientFunds() {
        try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
            paymentDTO.setUserSender(null);
            // Mock security context
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn("1");

            userSender.setBalance(100.0);

            when(userBalanceRepository.findByUserId(1L)).thenReturn(userSender);
            when(userBalanceRepository.findById(2L)).thenReturn(Optional.of(userReceiver));
            when(transactionService.processStatusType(paymentDTO.getAmount(), userSender.getBalance())).thenReturn(Status.REJECTED);
            when(paymentMapper.dtoToEntity(paymentDTO)).thenReturn(payment);
            when(paymentRepository.save(payment)).thenReturn(payment);

            PaymentResponse response = paymentService.pay(paymentDTO);

            assertNotNull(response);
            assertEquals("Transaction rejected. Insufficient funds in your account.", response.getReturnMessage());
            assertEquals(userSender, payment.getUserSender());
            assertEquals(userReceiver, payment.getUserReceiver());
            assertEquals(100.0, userSender.getBalance());
            assertEquals(1000.0, userReceiver.getBalance());

            verify(userBalanceRepository).findByUserId(1L);
            verify(userBalanceRepository).findById(2L);
            verify(paymentMapper).dtoToEntity(paymentDTO);
            verify(transactionService).processStatusType(paymentDTO.getAmount(), userSender.getBalance());
            verify(paymentRepository).save(payment);
        }
    }

    @Test
    void pay_shouldThrowException_whenSenderAndReceiverAreSame() {
        try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
            paymentDTO.setUserSender(null);
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn("1");

            paymentDTO.getUserReceiver().setUserId(1L);
            when(userBalanceRepository.findByUserId(1L)).thenReturn(userSender);
            when(userBalanceRepository.findById(1L)).thenReturn(Optional.of(userSender));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> paymentService.pay(paymentDTO));
            assertEquals("You cannot transfer money to yourself", exception.getMessage());

            verify(userBalanceRepository).findByUserId(1L);
            verify(userBalanceRepository).findById(1L);
            verify(transactionService, never()).processStatusType(anyDouble(), anyDouble());
            verify(paymentMapper, never()).dtoToEntity(any());
            verify(paymentRepository, never()).save(any());
        }
    }

    @Test
    void pay_shouldThrowException_whenReceiverNotFound() {
        try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
            paymentDTO.setUserSender(null);
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn("1");

            when(userBalanceRepository.findByUserId(1L)).thenReturn(userSender);
            when(userBalanceRepository.findById(2L)).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class, () -> paymentService.pay(paymentDTO));
            assertEquals("User Receiver with id: 2 not found!!", exception.getMessage());
        }
    }

    @Test
    void getAllPayments_shouldReturnListOfPaymentDTOs() {
        Payment payment1 = Payment.builder()
                .amount(1000.0)
                .timeOfPayment(currentDateTime)
                .userSender(userSender)
                .userReceiver(userReceiver)
                .build();

        Payment payment2 = Payment.builder()
                .amount(500.0)
                .timeOfPayment(currentDateTime.minusDays(1))
                .userSender(userReceiver)
                .userReceiver(userSender)
                .build();

        PaymentDTO paymentDTO1 = PaymentDTO.builder()
                .amount(1000.0)
                .timeOfPayment(currentDateTime)
                .userSender(userSenderDTO)
                .userReceiver(userReceiverDTO)
                .build();

        PaymentDTO paymentDTO2 = PaymentDTO.builder()
                .amount(500.0)
                .timeOfPayment(currentDateTime.minusDays(1))
                .userSender(userReceiverDTO)
                .userReceiver(userSenderDTO)
                .build();

        when(paymentRepository.findAll()).thenReturn(List.of(payment1, payment2));
        when(paymentMapper.entityToDto(payment1)).thenReturn(paymentDTO1);
        when(paymentMapper.entityToDto(payment2)).thenReturn(paymentDTO2);

        List<PaymentDTO> result = paymentService.getAllPayments();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(paymentDTO1.getAmount(), result.get(0).getAmount());
        assertEquals(paymentDTO1.getTimeOfPayment(), result.get(0).getTimeOfPayment());
        assertEquals(paymentDTO1.getUserSender().getUserId(), result.get(0).getUserSender().getUserId());
        assertEquals(paymentDTO1.getUserReceiver().getUserId(), result.get(0).getUserReceiver().getUserId());

        assertEquals(paymentDTO2.getAmount(), result.get(1).getAmount());
        assertEquals(paymentDTO2.getTimeOfPayment(), result.get(1).getTimeOfPayment());
        assertEquals(paymentDTO2.getUserSender().getUserId(), result.get(1).getUserSender().getUserId());
        assertEquals(paymentDTO2.getUserReceiver().getUserId(), result.get(1).getUserReceiver().getUserId());

        verify(paymentRepository, times(1)).findAll();
        verify(paymentMapper, times(1)).entityToDto(payment1);
        verify(paymentMapper, times(1)).entityToDto(payment2);
    }

}
