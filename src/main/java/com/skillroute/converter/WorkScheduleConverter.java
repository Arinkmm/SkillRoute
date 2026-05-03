package com.skillroute.converter;

import com.skillroute.model.WorkSchedule;
import com.skillroute.exception.DataMappingException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class WorkScheduleConverter implements AttributeConverter<WorkSchedule, String> {

    @Override
    public String convertToDatabaseColumn(WorkSchedule attribute) {
        if (attribute == null) return null;
        return attribute.name();
    }

    @Override
    public WorkSchedule convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return WorkSchedule.valueOf(dbData);
        } catch (IllegalArgumentException e) {
            throw new DataMappingException("Неизвестный тип графика в БД: " + dbData);
        }
    }
}