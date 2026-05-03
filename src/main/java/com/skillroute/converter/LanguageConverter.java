package com.skillroute.converter;

import com.skillroute.exception.DataMappingException;
import com.skillroute.model.Language;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LanguageConverter implements AttributeConverter<Language, String> {
    @Override
    public String convertToDatabaseColumn(Language attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public Language convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return Language.valueOf(dbData);
        } catch (IllegalArgumentException e) {
            throw new DataMappingException("Неизвестный язык в БД: " + dbData);
        }
    }
}