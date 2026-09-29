package com.da.da.entity.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentModeConverter implements AttributeConverter<PaymentMode, String> {

    @Override
    public String convertToDatabaseColumn(PaymentMode attribute) {
        return attribute != null ? attribute.name() : null;
    }

    @Override
    public PaymentMode convertToEntityAttribute(String dbData) {
        return PaymentMode.fromString(dbData);
    }
}
