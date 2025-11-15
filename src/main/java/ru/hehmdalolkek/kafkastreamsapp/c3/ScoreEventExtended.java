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
public class ScoreEventExtended implements Comparable<ScoreEventExtended> {

    public ScoreEventExtended(ScoreEventWithPlayer scoreEvent, Game game) {
        gameId = scoreEvent.getGameId();
        playerId = scoreEvent.getPlayerId();
        playerNickname = scoreEvent.getPlayerNickname();
        score = scoreEvent.getScore();
        gameName = game.getName();
    }

    private Long gameId;

    private Long playerId;

    private String playerNickname;

    private Long score;

    private String gameName;

    @Override
    public int compareTo(ScoreEventExtended o) {
        return Double.compare(o.getScore(), this.score);
    }
}
