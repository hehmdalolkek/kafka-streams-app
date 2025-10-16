package ru.hehmdalolkek.kafkastreamsapp;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Properties;

/**
 * @author Inna Badekha
 */
public class DslExample {

    public static void main(String[] args) {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<Void, String> stream = builder.stream("users");

        stream.foreach((key, value) -> {
            System.out.println("DSL: Hello, " + value);
        });

        Properties config = new Properties();
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "dsl-example");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        KafkaStreams streams = new KafkaStreams(builder.build(), config);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

}
