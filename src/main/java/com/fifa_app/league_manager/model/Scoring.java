package com.fifa_app.league_manager.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode
@ToString
public class Scoring {
    private String id;
    private boolean ownGoal;
    private int minuteOfGoal;

    private PlayerMatch playerMatch;
}
