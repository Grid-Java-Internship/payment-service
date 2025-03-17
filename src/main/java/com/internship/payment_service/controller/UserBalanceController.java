package com.internship.payment_service.controller;


import com.internship.payment_service.modelDTO.UserBalanceDTO;
import com.internship.payment_service.response.UserBalanceResponse;
import com.internship.payment_service.service.UserBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/v1/user/balance")
@RestController
@RequiredArgsConstructor
public class UserBalanceController {

    private final UserBalanceService userBalanceService;

    /**
     * Creates a new user balance.
     * <p>
     * This REST endpoint creates a new user balance with the given
     * {@link UserBalanceDTO}. The user balance is created with the given balance
     * and the user id is set to the id of the user.
     * <p>
     * The request body must contain a valid
     * {@link UserBalanceDTO} with the following properties:
     * <ul>
     * <li>userId: the id of the user. This field is optional and if not
     * provided the user id will be set to 0.</li>
     * <li>balance: the initial balance of the user. This field is optional and
     * if not provided the balance will be set to 0.</li>
     * </ul>
     * <p>
     * The response is a {@link ResponseEntity} with a body of type {@link String}
     * containing the message "Successfully added user with initial balance 0".
     * <p>
     * The following status codes are returned:
     * <ul>
     * <li>200: the user balance was created successfully.</li>
     * <li>400: the request body does not contain a valid
     * {@link UserBalanceDTO}</li>
     * <li>404: the user was not found.</li>
     *
     * @param userBalanceDTO the user balance to be created.
     * @return a response entity with the message "Successfully added user with
     * initial balance 0".
     */
    @PostMapping
    public ResponseEntity<String> addUserBalance(@RequestBody UserBalanceDTO userBalanceDTO) {
        return ResponseEntity.ok(userBalanceService.addUserBalance(userBalanceDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserBalanceResponse> getUserBalanceById(@PathVariable Long id) {
        return ResponseEntity.ok(userBalanceService.getUserBalanceById(id));
    }


}
