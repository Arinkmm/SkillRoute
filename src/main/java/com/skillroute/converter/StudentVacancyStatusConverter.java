package com.skillroute.converter;

import com.skillroute.exception.DataMappingException;
import com.skillroute.model.StudentVacancyStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StudentVacancyStatusConverter implements AttributeConverter<StudentVacancyStatus, String> {

    @Override
    public String convertToDatabaseColumn(StudentVacancyStatus attribute) {
        if (attribute == null) return null;
        return attribute.name();
    }

    @Override
    public StudentVacancyStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return StudentVacancyStatus.valueOf(dbData);
        } catch (IllegalArgumentException e) {
            throw new DataMappingException("Неизвестный статус отклика в БД: " + dbData);
        }
    }
}