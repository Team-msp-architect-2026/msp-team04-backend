package com.moment.momentbackend.recommendation.converter;

import com.moment.momentbackend.recommendation.enums.OnlinePreference;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class OnlinePreferenceConverter implements AttributeConverter<OnlinePreference, String> {

    @Override
    public String convertToDatabaseColumn(OnlinePreference attribute) {
        return attribute != null ? attribute.getCode() : null;
    }

    @Override
    public OnlinePreference convertToEntityAttribute(String dbData) {
        return OnlinePreference.fromCode(dbData);
    }
}
