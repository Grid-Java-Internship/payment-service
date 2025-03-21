package com.internship.payment_service.service;

import com.internship.payment_service.modelDTO.UserBalanceDTO;
import com.internship.payment_service.response.UserBalanceResponse;

public interface UserBalanceService {

    String addUserBalance(UserBalanceDTO userBalanceDTO);

    UserBalanceResponse getUserBalanceById(Long userId);

    Boolean deleteUserBalance(Long userId);
}
