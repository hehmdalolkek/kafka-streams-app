package ru.hehmdalolkek.kafkastreamsapp.c2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

/**
 * @author Inna Badekha
 */
public class TweetSerializer implements Serializer<Tweet> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String s, Tweet tweet) {
        if (tweet == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsBytes(tweet);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
