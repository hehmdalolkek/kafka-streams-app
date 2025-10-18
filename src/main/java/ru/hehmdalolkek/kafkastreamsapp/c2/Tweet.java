package ru.hehmdalolkek.kafkastreamsapp.c2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tweet {

    @JsonProperty("CreatedAt")
    private Long createdAt;

    @JsonProperty("Id")
    private Long id;

    @JsonProperty("Lang")
    private String lang;

    @JsonProperty("Retweet")
    private Boolean retweet;

    @JsonProperty("Text")
    private String text;

}
