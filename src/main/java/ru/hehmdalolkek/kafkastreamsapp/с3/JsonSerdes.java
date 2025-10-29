package ru.hehmdalolkek.kafkastreamsapp.с3;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import ru.hehmdalolkek.kafkastreamsapp.c2.Tweet;
import ru.hehmdalolkek.kafkastreamsapp.c2.TweetDeserializer;
import ru.hehmdalolkek.kafkastreamsapp.c2.TweetSerializer;

/**
 * @author Inna Badekha
 */
public class JsonSerdes<T> implements Serde<T> {

    private final Serializer<T> serializer;

    private final Deserializer<T> deserializer;

    public JsonSerdes(Class<T> targetClass) {
        this.serializer = new JsonSerializer<>();
        this.deserializer = new JsonDeserializer<>(targetClass);
    }

    @Override
    public Serializer<T> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<T> deserializer() {
        return deserializer;
    }

}
