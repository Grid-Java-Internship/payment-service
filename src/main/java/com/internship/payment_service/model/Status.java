package com.internship.payment_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum Status {


    ON_HOLD(2),
    REJECTED(1),
    COMPLETED(3);


    private final int id;

    public static Status fromId(int id) {
        return Arrays.stream(values())
                .filter(category -> category.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid status id: " + id));
    }

}
