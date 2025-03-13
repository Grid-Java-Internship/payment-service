package com.internship.payment_service.service;

import com.internship.payment_service.exception.NotFoundException;
import com.internship.payment_service.mapper.UserBalanceMapper;
import com.internship.payment_service.model.UserBalance;
import com.internship.payment_service.modelDTO.UserBalanceDTO;
import com.internship.payment_service.proxy.UserDTO;
import com.internship.payment_service.proxy.UserProxy;
import com.internship.payment_service.repository.UserBalanceRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserBalanceServiceImpl implements UserBalanceService{

    private final UserProxy userProxy;
    private final UserBalanceRepository userBalanceRepository;
    private final UserBalanceMapper userBalanceMapper;


    @Override
    public String addUserBalance(UserBalanceDTO userBalanceDTO) {

        UserDTO userDTO;
        log.info("UserBalanceDTO with id: {} and balance: {} ",userBalanceDTO.getUserId(),userBalanceDTO.getBalance());
        try {

            userDTO = userProxy.getUserById(userBalanceDTO.getUserId());
        } catch (FeignException exception) {
            throw new NotFoundException("User not found");
        }

        userBalanceDTO.setUserId(userDTO.getId());
        UserBalance userBalance=userBalanceMapper.dtoToEntity(userBalanceDTO);
        log.info("User balance with id: {} and balance: {}", userBalance.getUserId(), userBalance.getBalance());
        userBalanceRepository.save(userBalance);
        return "Successfully added user with initial balance 0";
    }
}
