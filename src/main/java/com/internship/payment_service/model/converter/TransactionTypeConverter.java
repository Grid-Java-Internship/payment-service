package com.internship.payment_service.model.converter;

import com.internship.payment_service.model.TransactionType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TransactionTypeConverter implements AttributeConverter<TransactionType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TransactionType transactionType) {
        if (transactionType == null) {
            return null;
        }
        return transactionType.getId();
    }

    @Override
    public TransactionType convertToEntityAttribute(Integer id) {
        if (id == null) {
            return null;
        }
        return TransactionType.fromId(id);
    }

}
