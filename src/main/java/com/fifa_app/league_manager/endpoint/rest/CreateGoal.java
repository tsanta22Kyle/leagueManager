package com.fifa_app.league_manager.endpoint.rest;


import lombok.*;

@AllArgsConstructor@NoArgsConstructor@Setter@Getter@EqualsAndHashCode@ToString
public class CreateGoal {
    private String ClubId;
    private String scorerId;
    private int minuteOfGoal;
}
