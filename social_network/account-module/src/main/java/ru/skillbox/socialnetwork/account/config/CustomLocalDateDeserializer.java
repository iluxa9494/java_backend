package ru.skillbox.socialnetwork.account.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CustomLocalDateDeserializer extends JsonDeserializer<LocalDate> {

    private static final DateTimeFormatter[] FORMATTERS = {
            DateTimeFormatter.ISO_LOCAL_DATE_TIME, // "2000-02-19T00:00:00.000Z"
            DateTimeFormatter.ISO_LOCAL_DATE       // "2000-02-19"
    };

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String dateString = p.getValueAsString();

        // Если "none" или пустое значение - возвращаем null
        if (dateString == null || dateString.trim().isEmpty() || "none".equalsIgnoreCase(dateString)) {
            return null;
        }

        // Пробуем разные форматы дат
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                // Для ISO_LOCAL_DATE_TIME берём только дату
                if (formatter == DateTimeFormatter.ISO_LOCAL_DATE_TIME) {
                    return LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE_TIME);
                }
                return LocalDate.parse(dateString, formatter);
            } catch (DateTimeParseException e) {
                // Пробуем следующий формат
            }
        }

        // Если ни один формат не подошёл
        return null;
    }
}