package com.skillroute.converter;

import com.skillroute.model.Direction;
import com.skillroute.exception.DataMappingException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class DirectionConverter implements AttributeConverter<Direction, String> {
    @Override
    public String convertToDatabaseColumn(Direction attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public Direction convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return Direction.valueOf(dbData);
        } catch (IllegalArgumentException e) {
            throw new DataMappingException("Неизвестное направление в БД: " + dbData);
        }
    }
}