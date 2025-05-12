package com.internship.payment_service.service;

import com.internship.payment_service.exception.NotFoundException;
import com.internship.payment_service.mapper.UserBalanceMapper;
import com.internship.payment_service.model.UserBalance;
import com.internship.payment_service.modelDTO.UserBalanceDTO;
import com.internship.payment_service.repository.UserBalanceRepository;
import com.internship.payment_service.response.UserBalanceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserBalanceServiceImpl implements UserBalanceService {

    private final UserBalanceRepository userBalanceRepository;
    private final UserBalanceMapper userBalanceMapper;


    /**
     * Add user balance to database.
     *
     * @param userBalanceDTO user balance to add
     * @return success message
     * @throws IllegalArgumentException if user balance with given id already exists
     * @throws NotFoundException        if user with given id not found
     */
    @Override
    public String addUserBalance(UserBalanceDTO userBalanceDTO) {

        if (userBalanceRepository.existsById(userBalanceDTO.getUserId())) {
            throw new IllegalArgumentException("User balance with id: " + userBalanceDTO.getUserId() + " already exists!!");
        }

        log.info("UserBalanceDTO with id: {} and balance: {} ", userBalanceDTO.getUserId(), userBalanceDTO.getBalance());

        userBalanceDTO.setUserId(userBalanceDTO.getUserId());
        UserBalance userBalance = userBalanceMapper.dtoToEntity(userBalanceDTO);
        log.info("User balance with id: {} and balance: {}", userBalance.getUserId(), userBalance.getBalance());
        userBalanceRepository.save(userBalance);
        return "Successfully added user with initial balance 0";
    }

    @Override
    public UserBalanceResponse getUserBalanceById(Long userId) {
        Optional<UserBalance> userBalance = Optional.ofNullable(userBalanceRepository.findByUserId(userId));
        if(userBalance.isEmpty()) {
            throw new NotFoundException("User with id: " + userId + " not found!!");
        }
        return UserBalanceResponse.builder()
                .userId(userBalance.get().getUserId())
                .balance(userBalance.get().getBalance())
                .build();
    }

    /**
     * Deletes the user balance associated with the given user ID.
     *
     * @param userId the ID of the user whose balance is to be deleted
     * @return true if the user balance was successfully deleted
     * @throws NotFoundException if the user balance with the given ID is not found
     */
    @Override
    public Boolean deleteUserBalance(Long userId) {
        UserBalance userBalance = userBalanceRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("UserBalance not found.")
        );

        userBalanceRepository.delete(userBalance);
        return true;
    }
}
