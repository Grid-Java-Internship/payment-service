package com.internship.payment_service.rabbitmq.consumer;

import com.internship.payment_service.model.UserBalance;
import com.internship.payment_service.rabbitmq.Message;
import com.internship.payment_service.repository.UserBalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteBalanceConsumer {

    private final UserBalanceRepository userBalanceRepository;

    /**
     * Consume a message containing a user id to delete the user balance associated
     * with that id.
     *
     * @param message the message containing the user id
     */
    @RabbitListener(queues = "${configs.rabbitmq.queues.deleteBalance}")
    public void consumeMessage(Message message) {
        log.info("Attempting to delete user balance with id: {}", message.getUserId());

        Optional<UserBalance> userBalance = userBalanceRepository.findById(message.getUserId());

        if (userBalance.isPresent()) {
            userBalanceRepository.delete(userBalance.get());
        } else {
            log.error("User balance with id {} not found.", message.getUserId());
        }
    }
}
