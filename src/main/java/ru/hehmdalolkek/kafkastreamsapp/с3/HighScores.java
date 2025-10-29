package ru.hehmdalolkek.kafkastreamsapp.с3;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.TreeSet;

/**
 * @author Inna Badekha
 */
@Data
public class HighScores {

    private final TreeSet<ScoreEventExtended> highScores = new TreeSet<>();

    public HighScores add(ScoreEventExtended event) {
        highScores.add(event);

        if (highScores.size() > 3) {
            highScores.remove(highScores.last());
        }

        return this;
    }

}
