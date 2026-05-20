package com.moment.momentbackend.recommendation.converter;

import com.moment.momentbackend.recommendation.enums.MonthlyBudget;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class MonthlyBudgetConverter implements AttributeConverter<MonthlyBudget, String> {

    @Override
    public String convertToDatabaseColumn(MonthlyBudget attribute) {
        return attribute != null ? attribute.getCode() : null;
    }

    @Override
    public MonthlyBudget convertToEntityAttribute(String dbData) {
        return MonthlyBudget.fromCode(dbData);
    }
}
