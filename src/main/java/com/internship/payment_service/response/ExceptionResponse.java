package com.internship.payment_service.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@Builder
public class ExceptionResponse {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Integer statusCode;

    @Builder.Default
    private final Boolean success = false;

    private List<String> messages;

    private final String timestamp = LocalDateTime.now().format(FORMAT);
}
