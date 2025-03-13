package com.internship.payment_service.modelDTO;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserBalanceDTO {

    @NotNull(message = "User id cannot be null")
    private Long userId;

    private Double balance;


}
