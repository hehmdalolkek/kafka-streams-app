
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
public class ScoreEventWithPlayer {

    public ScoreEventWithPlayer(ScoreEvent scoreEvent, Player player) {
        gameId = scoreEvent.getGameId();
        playerId = player.getPlayerId();
        playerNickname = player.getNickname();
        score = scoreEvent.getScore();
    }

    private Long gameId;

    private Long playerId;

    private String playerNickname;

    private Long score;

}
