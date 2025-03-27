package com.internship.payment_service.mapper;

import com.internship.payment_service.model.Payment;
import com.internship.payment_service.modelDTO.PaymentDTO;
import com.internship.payment_service.response.PaymentResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserBalanceMapper.class})
public interface PaymentMapper {

    Payment dtoToEntity(PaymentDTO paymentDTO);

    PaymentResponse entityToResponse(Payment payment);

    PaymentDTO entityToDto(Payment payment);
}
