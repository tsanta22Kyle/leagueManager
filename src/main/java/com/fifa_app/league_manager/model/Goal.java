package com.fifa_app.league_manager.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Setter
@EqualsAndHashCode
@ToString
public class Goal {
    private String id;
    private PlayerMatch playerMatch;
    private boolean ownGoal;
    private int minuteOfGoal;
    private ClubMatch clubMatch;
}
