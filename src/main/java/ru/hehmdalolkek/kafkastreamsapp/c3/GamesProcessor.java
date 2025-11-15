package ru.hehmdalolkek.kafkastreamsapp.c3;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.*;
import ru.hehmdalolkek.kafkastreamsapp.JsonSerdes;

import java.util.Properties;

/**
 * @author Inna Badekha
 */
public class GamesProcessor {

    public static void main(String[] args) {
        StreamsBuilder builder = new StreamsBuilder();


        KStream<Long, ScoreEvent> scoreEventStream = builder.stream(
                "score-events",
                Consumed.with(Serdes.ByteArray(), new JsonSerdes<>(ScoreEvent.class))
        ).selectKey((key, value) -> value.getPlayerId());

        KTable<Long, Player> playersTable = builder.table(
                "players",
                Consumed.with(Serdes.Long(), new JsonSerdes<>(Player.class))
        );

        GlobalKTable<Long, Game> gamesGlobalTable = builder.globalTable(
                "games",
                Consumed.with(Serdes.Long(), new JsonSerdes<>(Game.class))
        );

        Joined<Long, ScoreEvent, Player> playerJoinParams = Joined.with(
                Serdes.Long(),
                new JsonSerdes<>(ScoreEvent.class),
                new JsonSerdes<>(Player.class)
        );

        ValueJoiner<ScoreEvent, Player, ScoreEventWithPlayer> scorePlayerJoiner = ScoreEventWithPlayer::new;
        KStream<Long, ScoreEventWithPlayer> withPlayersStream =
                scoreEventStream.join(playersTable, scorePlayerJoiner, playerJoinParams);

        KeyValueMapper<Long, ScoreEventWithPlayer, Long> keyMapper =
                (leftKey, scoreWithPlayer) -> scoreWithPlayer.getPlayerId();

        ValueJoiner<ScoreEventWithPlayer, Game, ScoreEventExtended> scoreValueExtendedJoiner = ScoreEventExtended::new;
        KStream<Long, ScoreEventExtended> scoreValueExtendedStream =
                withPlayersStream.join(gamesGlobalTable, keyMapper, scoreValueExtendedJoiner);

        KGroupedStream<Long, ScoreEventExtended> grouped = scoreValueExtendedStream.groupBy(
                (key, value) -> value.getGameId(),
                Grouped.with(Serdes.Long(), new JsonSerdes<>(ScoreEventExtended.class))
        );

        Initializer<HighScores> highScoresInitializer = HighScores::new;
        Aggregator<Long, ScoreEventExtended, HighScores> highScoresAggregator = (key, value, aggregate) -> aggregate.add(value);

        KTable<Long, HighScores> highScoresTable = grouped.aggregate(
                highScoresInitializer,
                highScoresAggregator,
                Materialized.<Long, HighScores, KeyValueStore<Bytes, byte[]>>as("leaders")
                        .withKeySerde(Serdes.Long())
                        .withValueSerde(new JsonSerdes<>(HighScores.class))
        );


        Properties config = new Properties();
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "games");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        KafkaStreams streams = new KafkaStreams(builder.build(), config);
        streams.start();

        ReadOnlyKeyValueStore<Long, HighScores> stateStore = waitUntilStoreIsQueryable(
                streams,
                "leaders",
                QueryableStoreTypes.keyValueStore()
        );
        try (KeyValueIterator<Long, HighScores> iterator = stateStore.all()) {
            iterator.forEachRemaining(action -> System.out.println(action.value));
        }

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private static <T> T waitUntilStoreIsQueryable(
            KafkaStreams streams,
            String storeName,
            QueryableStoreType<T> storeType
    ) {
        while (true) {
            try {
                T store = streams.store(StoreQueryParameters.fromNameAndType(storeName, storeType));
                if (store != null) {
                    System.out.println("Store " + storeName + " is ready!");
                    return store;
                }
            } catch (InvalidStateStoreException e) {
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for store " + storeName, e);
            }
        }
    }

}
