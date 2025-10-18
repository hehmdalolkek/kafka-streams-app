package ru.hehmdalolkek.kafkastreamsapp.c2;

import com.mitchseymour.kafka.serialization.avro.AvroSerdes;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * @author Inna Badekha
 */
public class Branching {

    public static void main(String[] args) {
        StreamsBuilder builder = new StreamsBuilder();


        KStream<byte[], Tweet> stream = builder.stream("tweets", Consumed.with(Serdes.ByteArray(), new TweetSerdes()));

        stream.print(Printed.<byte[], Tweet>toSysOut().withLabel("tweets-stream"));

        KStream<byte[], Tweet> filtered = stream.filterNot((key, value) -> value.getRetweet());

        Map<String, KStream<byte[], Tweet>> branches = filtered.split(Named.as("branch-"))
                .branch((key, value) -> Objects.equals(value.getLang(), "en"))
                .branch((key, value) -> !Objects.equals(value.getLang(), "en"))
                .defaultBranch();
        KStream<byte[], Tweet> englishStream = branches.get("branch-1");
        KStream<byte[], Tweet> nonEnglishStream = branches.get("branch-2").mapValues(value -> {
            value.setText("Переведено");
            return value;
        });
        KStream<byte[], Tweet> merged = englishStream.merge(nonEnglishStream);

        KStream<byte[], AvroTweet> avroStream = merged.mapValues(value -> new AvroTweet(value.getText(), value.getCreatedAt()));

        avroStream.to(
                "tweets2",
                Produced.with(Serdes.ByteArray(), AvroSerdes.get(AvroTweet.class))
        );


        Properties config = new Properties();
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "branching");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        KafkaStreams streams = new KafkaStreams(builder.build(), config);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

}
