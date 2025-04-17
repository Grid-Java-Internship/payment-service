package com.internship.payment_service.rabbitmq.producer;

import com.internship.payment_service.model.Status;
import com.internship.payment_service.rabbitmq.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionProducer {
    private final AmqpTemplate amqpTemplate;

    public void send(String emailTo, Long transactionId, Status status) {

        Message message = makeAMessage(emailTo,transactionId,status);

    amqpTemplate.convertAndSend("holdTransactionQueue",message);
    }

    private Message makeAMessage(String emailTo, Long transactionId,Status status) {

        if(Status.ON_HOLD.equals(status)) {
            return Message
                    .builder()
                    .title("Confirm your transaction")
                    .content("Please confirm your transaction, until then it will be put ON HOLD! \n" +
                            "Link to confirm the transaction: http://localhost:8089/v1/transactions/confirm/" + transactionId)
                    .emailTo(emailTo)
                    .build();
        }

        return null;
    }
}
