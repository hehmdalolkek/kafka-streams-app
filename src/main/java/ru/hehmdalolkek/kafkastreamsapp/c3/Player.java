package ru.hehmdalolkek.kafkastreamsapp.c3;

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
public class Player {

    private Long playerId;

    private String nickname;

}
