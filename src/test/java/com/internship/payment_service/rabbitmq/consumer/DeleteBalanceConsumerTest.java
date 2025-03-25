package com.internship.payment_service.rabbitmq.consumer;

import com.internship.payment_service.model.UserBalance;
import com.internship.payment_service.rabbitmq.Message;
import com.internship.payment_service.repository.UserBalanceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import ch.qos.logback.core.read.ListAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.Logger;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteBalanceConsumerTest {

    @Mock
    private UserBalanceRepository userBalanceRepository;

    @InjectMocks
    private DeleteBalanceConsumer deleteBalanceConsumer;

    private ListAppender<ILoggingEvent> logWatcher;

    private Message message;
    private UserBalance userBalance;

    @BeforeEach
    void beforeEach() {
        message = new Message(1L);

        userBalance = UserBalance.builder()
                .userId(1L)
                .balance(0.0)
                .build();

        logWatcher = new ListAppender<>();
        logWatcher.start();
        ((Logger) LoggerFactory.getLogger(DeleteBalanceConsumer.class)).addAppender(logWatcher);
    }

    @Test
    void consumeMessage_shouldDeleteBalance_whenBalanceExists() {

        when(userBalanceRepository.findById(1L)).thenReturn(Optional.of(userBalance));
        doNothing().when(userBalanceRepository).delete(userBalance);

        deleteBalanceConsumer.consumeMessage(message);

        verify(userBalanceRepository, times(1)).findById(1L);
        verify(userBalanceRepository, times(1)).delete(userBalance);
    }

    @Test
    void consumeMessage_shouldNotDeleteUser_whenUserDoesNotExist() {

        when(userBalanceRepository.findById(1L)).thenReturn(Optional.empty());

        deleteBalanceConsumer.consumeMessage(message);

        verify(userBalanceRepository, times(1)).findById(1L);
        verify(userBalanceRepository, never()).delete(any());
        assertEquals("User balance with id 1 not found.", logWatcher.list.get(1).getFormattedMessage());
    }

    @AfterEach
    void afterEach() {
        assertEquals("Attempting to delete user balance with id: 1", logWatcher.list.get(0).getFormattedMessage());
    }
}