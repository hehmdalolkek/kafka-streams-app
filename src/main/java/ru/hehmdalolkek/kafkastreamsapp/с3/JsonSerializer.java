package ru.hehmdalolkek.kafkastreamsapp.с3;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

/**
 * @author Inna Badekha
 */
public class JsonSerializer<T> implements Serializer<T> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String s, T t) {
        if (t == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsBytes(t);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
