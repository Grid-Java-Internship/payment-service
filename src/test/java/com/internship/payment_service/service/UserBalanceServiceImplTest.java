package com.internship.payment_service.service;

import com.internship.payment_service.exception.NotFoundException;
import com.internship.payment_service.mapper.UserBalanceMapper;
import com.internship.payment_service.model.UserBalance;
import com.internship.payment_service.modelDTO.UserBalanceDTO;
import com.internship.payment_service.proxy.UserDTO;
import com.internship.payment_service.repository.UserBalanceRepository;
import com.internship.payment_service.response.UserBalanceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserBalanceServiceImplTest {
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

        when(userBalanceMapper.dtoToEntity(userBalanceDTO)).thenReturn(userBalance);
        when(userBalanceRepository.save(userBalance)).thenReturn(userBalance);

        String response = userBalanceService.addUserBalance(userBalanceDTO);

        assertNotNull(response);
        assertEquals("Successfully added user with initial balance 0", response);

        verify(userBalanceMapper).dtoToEntity(userBalanceDTO);
        verify(userBalanceRepository).save(userBalance);

    }

    @Test
    void addUserBalance_shouldThrowNotFoundException_whenUserNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userBalanceService.addUserBalance(userBalanceDTO));

        assertEquals("User not found", exception.getMessage());
        verify(userBalanceRepository, never()).save(any());
    }

    @Test
    void addUserBalance_shouldThrowIllegalArgumentException_whenUserBalanceAlreadyExists() {

        when(userBalanceRepository.existsById(1L)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userBalanceService.addUserBalance(userBalanceDTO));

        assertEquals("User balance with id: 1 already exists!!", exception.getMessage());


        verify(userBalanceRepository, never()).save(any());
        verify(userBalanceMapper, never()).dtoToEntity(userBalanceDTO);
    }

    @Test
    void getUserById_shouldReturnUserBalance_whenUserExists() {

        when(userBalanceRepository.findByUserId(1L)).thenReturn(userBalance);

        UserBalanceResponse result = userBalanceService.getUserBalanceById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(0.0, result.getBalance());

        verify(userBalanceRepository, times(1)).findByUserId(1L);
    }

    @Test
    void getUserById_shouldThrowNotFoundException_whenUserNotFound() {

        when(userBalanceRepository.findByUserId(1L)).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userBalanceService.getUserBalanceById(1L));

        assertEquals("User with id: 1 not found!!", exception.getMessage());

        verify(userBalanceRepository, times(1)).findByUserId(1L);
    }

    @Test
    void deleteUserBalance_shouldDeleteUserBalance_whenItExists() {
        when(userBalanceRepository.findById(1L)).thenReturn(Optional.of(userBalance));

        doNothing().when(userBalanceRepository).delete(userBalance);

        boolean result = userBalanceService.deleteUserBalance(1L);

        assertTrue(result);
        verify(userBalanceRepository, times(1)).findById(1L);
        verify(userBalanceRepository, times(1)).delete(userBalance);
    }

    @Test
    void deleteUserBalance_shouldThrowException_whenUserBalanceNotFound() {
        when(userBalanceRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userBalanceService.deleteUserBalance(1L));

        assertEquals("UserBalance not found.", exception.getMessage());

        verify(userBalanceRepository, times(1)).findById(1L);
        verify(userBalanceRepository, never()).delete(userBalance);
    }
}