package com.skillroute.converter;

import com.skillroute.model.VacancyStatus;
import com.skillroute.exception.DataMappingException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class VacancyStatusConverter implements AttributeConverter<VacancyStatus, String> {

    @Override
    public String convertToDatabaseColumn(VacancyStatus attribute) {
        if (attribute == null) return null;
        return attribute.name();
    }

    @Override
    public VacancyStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return VacancyStatus.valueOf(dbData);
        } catch (IllegalArgumentException e) {
            throw new DataMappingException("Некорректный статус вакансии в БД: " + dbData);
        }
    }
}