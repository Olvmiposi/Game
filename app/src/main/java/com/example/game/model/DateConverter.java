package com.example.game.model;

import androidx.room.TypeConverter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonParser jsonparser,
                            DeserializationContext deserializationcontext) throws IOException, JsonProcessingException {

        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.FRANCE);
        String dateStr = jsonparser.getText();
        try {
            return (Date)formatter.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
