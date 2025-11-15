package ru.hehmdalolkek.kafkastreamsapp.c4;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

import java.time.ZoneOffset;

/**
 * @author Inna Badekha
 */
public class CustomTimestampExtractor implements TimestampExtractor {
    @Override
    public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
        Indicator value = (Indicator) record.value();
        if (value != null && value.getTimestamp() != null) {
            return value.getTimestamp().toEpochSecond(ZoneOffset.UTC);
        }
        return partitionTime;
    }
}
