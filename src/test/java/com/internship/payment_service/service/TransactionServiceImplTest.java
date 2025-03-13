package com.internship.payment_service.service;

import com.internship.payment_service.exception.NotFoundException;
import com.internship.payment_service.mapper.TransactionMapper;
import com.internship.payment_service.model.Status;
import com.internship.payment_service.model.Transaction;
import com.internship.payment_service.model.TransactionType;
import com.internship.payment_service.model.UserBalance;
import com.internship.payment_service.modelDTO.TransactionDTO;
import com.internship.payment_service.modelDTO.UserBalanceDTO;
import com.internship.payment_service.repository.TransactionRepository;
import com.internship.payment_service.repository.UserBalanceRepository;
import com.internship.payment_service.response.TransactionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

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

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private TransactionDTO transactionDTO;
    private UserBalance userBalance;
    private Transaction transaction;
    private final LocalDateTime currentDateTime=LocalDateTime.now();

    @BeforeEach
    void beforeEach(){

        UserBalanceDTO userBalanceDTO = UserBalanceDTO.builder()
                .userId(1L)
                .balance(5000.0)
                .build();

        transactionDTO = TransactionDTO.builder()
                .userBalance(userBalanceDTO)
                .transactionType(TransactionType.PAYPAL)
                .amount(5000.0)
                .timeOfTransaction(currentDateTime)
                .build();

        userBalance = UserBalance.builder()
                .userId(1L)
                .balance(5000.0)
                .build();

        transaction = Transaction.builder()
                .userBalance(userBalance)
                .transactionType(TransactionType.PAYPAL)
                .amount(5000.0)
                .timeOfTransaction(currentDateTime)
                .build();

    }


    @Test
    void deposit_shouldAddAmountAndReturnSuccessMessage_whenAmountIsLessOrEqual100000() {



        when(userBalanceRepository.findById(1L)).thenReturn(Optional.of(userBalance));
        when(transactionMapper.dtoToEntity(transactionDTO)).thenReturn(transaction);
        when(transactionRepository.save(transaction)).thenReturn(transaction);


        TransactionResponse response = transactionService.deposit(transactionDTO);

        assertNotNull(response);
        assertEquals(Status.COMPLETED, transactionDTO.getStatus());
        assertEquals("You have successfully added 5000.0 credits. New balance: 10000.0", response.getReturnMessage());

        verify(userBalanceRepository).findById(1L);
        verify(transactionMapper).dtoToEntity(transactionDTO);
        verify(transactionRepository).save(transaction);



    }

    @Test
    void deposit_shouldThrowNotFoundException_whenUserBalanceNotFound() {

        when(userBalanceRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> transactionService.deposit(transactionDTO));

        assertEquals("User with id: 1 not found!!", exception.getMessage());

        verify(userBalanceRepository).findById(1L);
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void deposit_shouldAddAmountAndWaitForApproval_WithStatusOnHold(){

        transactionDTO.setAmount(500000.0);

        when(userBalanceRepository.findById(1L)).thenReturn(Optional.of(userBalance));
        when(transactionMapper.dtoToEntity(transactionDTO)).thenReturn(transaction);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        TransactionResponse response = transactionService.deposit(transactionDTO);

        assertNotNull(response);
        assertEquals(Status.ON_HOLD, transactionDTO.getStatus());
        assertEquals("You have successfully added 500000.0 credits. New balance: 505000.0", response.getReturnMessage());

        verify(userBalanceRepository).findById(1L);
        verify(transactionMapper).dtoToEntity(transactionDTO);
        verify(transactionRepository).save(transaction);

    }


}