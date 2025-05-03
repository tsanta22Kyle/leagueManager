package com.fifa_app.league_manager.model;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EqualsAndHashCode
@ToString
public class ClubParticipation {
    private String id;
    private Club club;
    private Season season;
   private int points  ;
    private int wins;
    private int draws;
    private int losses;
    private int scoredGoals;
    private int concededGoals;
    private int cleanSheetNumber;
}
