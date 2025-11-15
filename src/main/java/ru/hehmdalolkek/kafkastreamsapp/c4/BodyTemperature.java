package ru.hehmdalolkek.kafkastreamsapp.c4;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Inna Badekha
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BodyTemperature implements Indicator {

    private LocalDateTime timestamp;

    private String unit;

    private Double temperature;

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
