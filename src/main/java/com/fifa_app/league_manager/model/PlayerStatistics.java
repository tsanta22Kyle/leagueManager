package com.fifa_app.league_manager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class PlayerStatistics {
    @JsonIgnore
    private Player player;
    @JsonIgnore
    private Season season;
    private int scoredGoals;
    private PlayingTime playingTime;
}
