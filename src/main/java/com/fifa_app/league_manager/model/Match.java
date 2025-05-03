package com.fifa_app.league_manager.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Setter
@EqualsAndHashCode
@ToString
public class Match {
    private String id;
    private ClubMatch clubPlayingHome;
    private ClubMatch clubPlayingAway;
    private String stadium;
    private Instant matchDatetime;
    private Status actualStatus;

    //@JsonIgnore
    //private List<PlayerMatch> playerMatches;
    @JsonProperty("stadium")
    public String getStadium() {
        if(clubPlayingHome == null) return null;
        return clubPlayingHome.getClub().getStadium();
    }
    @JsonIgnore
    private Season season;

    public Match(String matchId) {
        this.id = matchId;
    }
}
