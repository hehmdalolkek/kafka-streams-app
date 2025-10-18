package ru.hehmdalolkek.kafkastreamsapp.c2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

/**
 * @author Inna Badekha
 */
public class TweetDeserializer implements Deserializer<Tweet> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Tweet deserialize(String s, byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return objectMapper.readValue(bytes, Tweet.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
