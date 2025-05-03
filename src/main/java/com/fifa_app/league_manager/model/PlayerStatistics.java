package com.fifa_app.league_manager.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class PlayerStatistics {
    private int scoredGoals;
    private PlayingTime playingTime;
}
