package com.internship.payment_service.rabbitmq;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private Long userId;

    private String emailTo;

    private String title;

    private String content;

    public Message(Long userId){
        this.userId = userId;
    }

}
