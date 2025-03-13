package com.internship.payment_service.mapper;

import com.internship.payment_service.model.UserBalance;
import com.internship.payment_service.modelDTO.UserBalanceDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserBalanceMapper {

    UserBalance dtoToEntity(UserBalanceDTO userBalanceDTO);
}
