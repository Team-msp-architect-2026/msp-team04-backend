package com.moment.momentbackend.recommendation.converter;

import com.moment.momentbackend.recommendation.enums.MoveTime;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class MoveTimeConverter implements AttributeConverter<MoveTime, String> {

    @Override
    public String convertToDatabaseColumn(MoveTime attribute) {
        return attribute != null ? attribute.getCode() : null;
    }

    @Override
    public MoveTime convertToEntityAttribute(String dbData) {
        return MoveTime.fromCode(dbData);
    }
}
