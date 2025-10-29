package ru.hehmdalolkek.kafkastreamsapp.с3;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

/**
 * @author Inna Badekha
 */
public class JsonDeserializer<T> implements Deserializer<T> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Class<T> targetClass;

    public JsonDeserializer(Class<T> targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    public T deserialize(String s, byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return objectMapper.readValue(bytes, targetClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
