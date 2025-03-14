package com.internship.payment_service.controller;


import com.internship.payment_service.modelDTO.UserBalanceDTO;
import com.internship.payment_service.service.UserBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/users")
@RestController
@RequiredArgsConstructor
public class UserBalanceController {

    private final UserBalanceService userBalanceService;

    @PostMapping("/addUserBalance")
    public ResponseEntity<String> addUserBalance(@RequestBody UserBalanceDTO userBalanceDTO) {

        return ResponseEntity.ok(userBalanceService.addUserBalance(userBalanceDTO));
    }
}
