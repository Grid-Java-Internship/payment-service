package com.internship.payment_service.service;

import com.internship.payment_service.exception.NotFoundException;
import com.internship.payment_service.mapper.TransactionMapper;
import com.internship.payment_service.model.Status;
import com.internship.payment_service.model.Transaction;
import com.internship.payment_service.model.TransactionType;
import com.internship.payment_service.model.UserBalance;
import com.internship.payment_service.modelDTO.TransactionDTO;
import com.internship.payment_service.proxy.UserDTO;
import com.internship.payment_service.proxy.UserProxy;
import com.internship.payment_service.rabbitmq.producer.TransactionProducer;
import com.internship.payment_service.repository.TransactionRepository;
import com.internship.payment_service.repository.UserBalanceRepository;
import com.internship.payment_service.response.TransactionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserBalanceRepository userBalanceRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private TransactionProducer transactionProducer;

    @Mock
    private UserProxy userProxy;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private TransactionDTO transactionDTO;
    private UserBalance userBalance;
    private Transaction transaction;
    private final LocalDateTime currentDateTime = LocalDateTime.now();
    private final Long USER_ID = 1L;
    private UserDTO userDTO;

    private void mockSecurityContext(String userId) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userId);
        SecurityContextHolder.setContext(securityContext);
    }

    @BeforeEach
    void beforeEach() {
        SecurityContextHolder.clearContext();

        transactionDTO = TransactionDTO.builder()
                .transactionType(TransactionType.PAYPAL)
                .amount(5000.0)
                .timeOfTransaction(currentDateTime)
                .build();

        userBalance = UserBalance.builder()
                .userId(1L)
                .balance(5000.0)
                .build();

        transaction = Transaction.builder()
                .transactionId(1L)
                .userBalance(userBalance)
                .transactionType(TransactionType.PAYPAL)
                .amount(5000.0)
                .timeOfTransaction(currentDateTime)
                .build();

        userDTO = UserDTO.builder()
                .id(USER_ID)
                .email("test@user.com")
                .build();

    }


    @Test
    void deposit_shouldAddAmountAndReturnSuccessMessage_whenAmountIsLessOrEqual100000() {
        mockSecurityContext(String.valueOf(USER_ID));
        double initialBalance = userBalance.getBalance();
        double depositAmount = transactionDTO.getAmount();
        double expectedNewBalance = initialBalance + depositAmount;
        transaction.setStatus(Status.COMPLETED);

        when(userBalanceRepository.findByUserId(USER_ID)).thenReturn(userBalance);
        when(transactionMapper.dtoToEntity(transactionDTO)).thenReturn(transaction);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponse response = transactionService.deposit(transactionDTO);

        assertNotNull(response);
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());
        assertEquals(Status.COMPLETED, transactionCaptor.getValue().getStatus());
        assertEquals(Status.COMPLETED,transactionDTO.getStatus());

        assertEquals(expectedNewBalance, userBalance.getBalance());
        assertEquals("You have successfully added " + depositAmount + " credits. New balance: " + expectedNewBalance, response.getReturnMessage());

        verify(userBalanceRepository).findByUserId(USER_ID);
        verify(transactionMapper).dtoToEntity(transactionDTO);
        verifyNoInteractions(userProxy, transactionProducer);
    }


    @Test
    void deposit_shouldGoToOnHold_AndCallProxyAndProducer_WhenAmountIsLarge() {
        mockSecurityContext(String.valueOf(USER_ID));
        double highAmount = 500000.0;
        transactionDTO.setAmount(highAmount);
        transaction.setStatus(Status.ON_HOLD);
        transaction.setAmount(highAmount);

        when(userBalanceRepository.findByUserId(USER_ID)).thenReturn(userBalance);
        when(transactionMapper.dtoToEntity(transactionDTO)).thenReturn(transaction);
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        when(transactionRepository.save(transactionCaptor.capture())).thenReturn(transaction);

        when(userProxy.getUserById(USER_ID)).thenReturn(userDTO);
        doNothing().when(transactionProducer).send(anyString(), anyLong(), any(Status.class));

        TransactionResponse response = transactionService.deposit(transactionDTO);

        assertNotNull(response);
        assertEquals("Your transaction is on hold. Please confirm it on mail", response.getReturnMessage());
        assertEquals(Status.ON_HOLD, transactionCaptor.getValue().getStatus());
        assertEquals(5000.0, userBalance.getBalance());

        verify(userBalanceRepository).findByUserId(USER_ID);
        verify(transactionMapper).dtoToEntity(transactionDTO);
        verify(transactionRepository).save(any(Transaction.class));
        verify(userProxy).getUserById(USER_ID);
        verify(transactionProducer).send(userDTO.getEmail(), transaction.getTransactionId(), Status.ON_HOLD);
    }

    @Test
    void withdraw_shouldCompleteTransaction_whenSufficientBalance() {
        mockSecurityContext(String.valueOf(USER_ID));
        double initialBalance = userBalance.getBalance();
        double withdrawAmount = transactionDTO.getAmount();
        double expectedNewBalance = initialBalance - withdrawAmount;
        transaction.setStatus(Status.COMPLETED);

        when(userBalanceRepository.findByUserId(USER_ID)).thenReturn(userBalance);
        when(transactionMapper.dtoToEntity(transactionDTO)).thenReturn(transaction);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponse response = transactionService.withdraw(transactionDTO);

        assertNotNull(response);
        assertEquals(Status.COMPLETED, transactionDTO.getStatus());
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());
        assertEquals(Status.COMPLETED, transactionCaptor.getValue().getStatus());

        assertEquals(expectedNewBalance, userBalance.getBalance());
        assertEquals("You have successfully withdrawn " + withdrawAmount + " credits. New balance: " + expectedNewBalance, response.getReturnMessage());

        verify(userBalanceRepository).findByUserId(USER_ID);
        verify(transactionMapper).dtoToEntity(transactionDTO);
        verifyNoInteractions(userProxy, transactionProducer);
    }

    @Test
    void withdraw_shouldRejectTransaction_whenInsufficientBalance() {
        mockSecurityContext(String.valueOf(USER_ID));
        userBalance.setBalance(500.0);
        double initialBalance = userBalance.getBalance();

        transaction.setStatus(Status.REJECTED);

        when(userBalanceRepository.findByUserId(USER_ID)).thenReturn(userBalance);
        when(transactionMapper.dtoToEntity(transactionDTO)).thenReturn(transaction);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponse response = transactionService.withdraw(transactionDTO);

        assertNotNull(response);
        assertEquals(Status.REJECTED, transactionDTO.getStatus());
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());
        assertEquals(Status.REJECTED, transactionCaptor.getValue().getStatus());

        assertEquals(initialBalance, userBalance.getBalance());
        assertEquals("Transaction rejected. Insufficient funds in your account.", response.getReturnMessage());

        verify(userBalanceRepository).findByUserId(USER_ID);
        verify(transactionMapper).dtoToEntity(transactionDTO);
        verifyNoInteractions(userProxy, transactionProducer);
    }


    @Test
    void depositOnHold_ThrowsNotFoundException_WhenUserProxyFails() {
        mockSecurityContext(String.valueOf(USER_ID));
        double highAmount = 500000.0;
        transactionDTO.setAmount(highAmount);

        when(userBalanceRepository.findByUserId(USER_ID)).thenReturn(userBalance);
        when(userProxy.getUserById(USER_ID)).thenThrow(new NotFoundException("User proxy failed"));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            transactionService.deposit(transactionDTO);
        });
        assertEquals("User proxy failed", exception.getMessage());

        verify(userBalanceRepository).findByUserId(USER_ID);
        verify(userProxy).getUserById(USER_ID);
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(transactionProducer, never()).send(anyString(), anyLong(), any(Status.class));
        verifyNoInteractions(transactionMapper);
    }

    @Test
    void depositOnHold_Success_CallsSaveAndProducer() {
        mockSecurityContext(String.valueOf(USER_ID));
        double highAmount = 500000.0;
        transactionDTO.setAmount(highAmount);
        transaction.setStatus(Status.ON_HOLD);
        transaction.setAmount(highAmount);

        when(userBalanceRepository.findByUserId(USER_ID)).thenReturn(userBalance);
        when(userProxy.getUserById(USER_ID)).thenReturn(userDTO);
        when(transactionMapper.dtoToEntity(transactionDTO)).thenReturn(transaction);
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        when(transactionRepository.save(transactionCaptor.capture())).thenReturn(transaction);
        doNothing().when(transactionProducer).send(anyString(), anyLong(), any(Status.class));


        TransactionResponse response = transactionService.deposit(transactionDTO);

        assertNotNull(response);
        assertEquals("Your transaction is on hold. Please confirm it on mail", response.getReturnMessage());
        assertEquals(Status.ON_HOLD, transactionCaptor.getValue().getStatus());

        verify(userBalanceRepository).findByUserId(USER_ID);
        verify(userProxy).getUserById(USER_ID);
        verify(transactionMapper).dtoToEntity(transactionDTO);
        verify(transactionRepository).save(any(Transaction.class));
        verify(transactionProducer).send(userDTO.getEmail(), transaction.getTransactionId(), Status.ON_HOLD);
    }


}