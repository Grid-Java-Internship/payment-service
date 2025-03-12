package com.internship.payment_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum TransactionType {


    CREDIT_CARD(1),
    DEBIT_CARD(2),
    PAYPAL(3);


    private final int id;

    public static TransactionType fromId(int id) {
        return Arrays.stream(values())
                .filter(category -> category.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid status id: " + id));
    }
}
