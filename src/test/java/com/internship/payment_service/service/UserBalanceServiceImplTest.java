package com.internship.payment_service.service;

import com.internship.payment_service.exception.NotFoundException;
import com.internship.payment_service.mapper.UserBalanceMapper;
import com.internship.payment_service.model.UserBalance;
import com.internship.payment_service.modelDTO.UserBalanceDTO;
import com.internship.payment_service.proxy.UserDTO;
import com.internship.payment_service.proxy.UserProxy;
import com.internship.payment_service.repository.UserBalanceRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserBalanceServiceImplTest {

    @Mock
    private UserProxy userProxy;

    @Mock
    private UserBalanceRepository userBalanceRepository;

    @Mock
    private UserBalanceMapper userBalanceMapper;

    @InjectMocks
    private UserBalanceServiceImpl userBalanceService;

    private UserBalanceDTO userBalanceDTO;
    private UserDTO userDTO;
    private UserBalance userBalance;


    @BeforeEach
    void setUp() {

        userBalanceDTO = UserBalanceDTO.builder()
                .userId(1L)
                .balance(0.0)
                .build();

        userDTO = UserDTO.builder()
                .id(1L)
                .build();

        userBalance = UserBalance.builder()
                .userId(1L)
                .balance(0.0)
                .build();


    }

    @Test
    void addUserBalance_shouldAddBalanceSuccessfully_whenUserExists() {

        when(userProxy.getUserById(userBalanceDTO.getUserId())).thenReturn(userDTO);
        when(userBalanceMapper.dtoToEntity(userBalanceDTO)).thenReturn(userBalance);
        when(userBalanceRepository.save(userBalance)).thenReturn(userBalance);

        String response= userBalanceService.addUserBalance(userBalanceDTO);

        assertNotNull(response);
        assertEquals("Successfully added user with initial balance 0",response);

        verify(userProxy).getUserById(userBalance.getUserId());
        verify(userBalanceMapper).dtoToEntity(userBalanceDTO);
        verify(userBalanceRepository).save(userBalance);

    }
    @Test
    void addUserBalance_shouldThrowNotFoundException_whenUserNotFound() {

        when(userProxy.getUserById(1L)).thenThrow(FeignException.NotFound.class);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userBalanceService.addUserBalance(userBalanceDTO));

        assertEquals("User not found", exception.getMessage());
        verify(userProxy).getUserById(1L);
        verify(userBalanceRepository, never()).save(any());
    }

}