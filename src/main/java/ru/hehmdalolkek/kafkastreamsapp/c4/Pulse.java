package ru.hehmdalolkek.kafkastreamsapp.c4;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Inna Badekha
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pulse implements Indicator {

    private LocalDateTime timestamp;

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
