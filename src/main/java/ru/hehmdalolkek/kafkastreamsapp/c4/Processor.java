package ru.hehmdalolkek.kafkastreamsapp.c4;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.kstream.internals.TimeWindow;
import ru.hehmdalolkek.kafkastreamsapp.JsonSerdes;

import java.time.Duration;
import java.util.Properties;

/**
 * @author Inna Badekha
 */
public class Processor {

    public static void main(String[] args) {
        StreamsBuilder builder = new StreamsBuilder();


        KStream<String, Pulse> pulseStream = builder.stream(
                "pulse-events",
                Consumed.with(
                        Serdes.String(),
                        new JsonSerdes<>(Pulse.class)
                ).withTimestampExtractor(new CustomTimestampExtractor())
        );
        KStream<String, BodyTemperature> bodyTempStream = builder.stream(
                "body-temperature-events",
                Consumed.with(
                        Serdes.String(),
                        new JsonSerdes<>(BodyTemperature.class)
                ).withTimestampExtractor(new CustomTimestampExtractor())
        );

        TimeWindows pulseWindow = TimeWindows.ofSizeAndGrace(
                Duration.ofSeconds(60),
                Duration.ofSeconds(5)
        );
        KTable<Windowed<String>, Long> pulseCounts = pulseStream
                .groupByKey()
                .windowedBy(pulseWindow)
                .count(Materialized.as("pulse-counts"))
                .suppress(
                        Suppressed.untilWindowCloses(
                                Suppressed.BufferConfig.unbounded().shutDownWhenFull()
                        )
                );
        pulseCounts.toStream()
                .print(Printed.<Windowed<String>,Long>toSysOut().withLabel("pulse-counts"));
        KStream<String, Long> highPulse = pulseCounts.toStream()
                .filter((windowedKey, value) -> value >= 100)
                .map((windowedKey, value) -> KeyValue.pair(windowedKey.key(), value));
        KStream<String, BodyTemperature> highTemperature = bodyTempStream
                .filter((key, value) -> value.getTemperature() >= 37.5);

        StreamJoined<String, Long, BodyTemperature> joinParams =
                StreamJoined.with(Serdes.String(), Serdes.Long(), new JsonSerdes<>(BodyTemperature.class));
        JoinWindows joinWindows = JoinWindows.ofTimeDifferenceAndGrace(
                Duration.ofSeconds(60),
                Duration.ofSeconds(10)
        );
        ValueJoiner<Long, BodyTemperature, CombinedIndicators> indicatorsJoiner =
                (pulseRate, bodyTemp) -> new CombinedIndicators(pulseRate.intValue(), bodyTemp);
        KStream<String, CombinedIndicators> highIndicatorsStream =
                highPulse.join(highTemperature, indicatorsJoiner, joinWindows, joinParams);
        highIndicatorsStream.to(
                "alerts",
                Produced.with(Serdes.String(), new JsonSerdes<>(CombinedIndicators.class))
        );

        Properties config = new Properties();
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "pulse");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        KafkaStreams streams = new KafkaStreams(builder.build(), config);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

}
