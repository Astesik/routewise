package com.example.ioproject.common.jpa;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.postgresql.util.PGobject;

@Converter(autoApply = false) // włączasz ręcznie na polu
public class JsonbStringConverter implements AttributeConverter<String, PGobject> {

    @Override
    public PGobject convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        try {
            PGobject out = new PGobject();
            out.setType("jsonb");
            out.setValue(attribute);
            return out;
        } catch (Exception e) {
            throw new IllegalArgumentException("Nie można przekonwertować JSON -> PGobject(jsonb)", e);
        }
    }

    @Override
    public String convertToEntityAttribute(PGobject dbData) {
        return dbData != null ? dbData.getValue() : null;
    }
}
