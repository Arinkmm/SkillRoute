package com.skillroute.converter;

import com.skillroute.model.Role;
import com.skillroute.exception.DataMappingException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role attribute) {
        if (attribute == null) return null;
        return attribute.name();
    }

    @Override
    public Role convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return Role.valueOf(dbData);
        } catch (IllegalArgumentException e) {
            throw new DataMappingException("Обнаружена недопустимая роль: " + dbData);
        }
    }
}