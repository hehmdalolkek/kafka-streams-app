package ru.hehmdalolkek.kafkastreamsapp.c4;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Inna Badekha
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CombinedIndicators {

    private Integer pulse;

    private BodyTemperature bodyTemperature;

}
