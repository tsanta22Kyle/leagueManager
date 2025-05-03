package com.fifa_app.league_manager.endpoint.rest;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode
@ToString
public class ScorerRest {
    private PlayerRest player;
    private int minuteOfGoal;
    private boolean ownGoal;
}
