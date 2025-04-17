package com.internship.payment_service.mapper;

import com.internship.payment_service.model.Transaction;
import com.internship.payment_service.modelDTO.TransactionDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserBalanceMapper.class})
public interface TransactionMapper {

    Transaction dtoToEntity(TransactionDTO transactionDTO);

    TransactionDTO entityToDto(Transaction transaction);
}
