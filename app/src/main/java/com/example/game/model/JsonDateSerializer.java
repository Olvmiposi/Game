package com.example.game.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JsonDateSerializer extends JsonSerializer<Date> {
    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

    @Override
    public void serialize(final Date date, final JsonGenerator gen, final SerializerProvider provider) throws IOException, JsonProcessingException {

        String dateString = format.format(date);
        gen.writeString(dateString);
    }

}