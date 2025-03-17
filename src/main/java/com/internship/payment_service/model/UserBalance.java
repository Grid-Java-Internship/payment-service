package com.internship.payment_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserBalance {


    @NotNull(message = "User cannot be null!!")
    @Id
    @Column(nullable = false, updatable = false)
    private Long userId;

    @PositiveOrZero(message = "Balance must be greater than or equal zero!!")
    private Double balance;


}
