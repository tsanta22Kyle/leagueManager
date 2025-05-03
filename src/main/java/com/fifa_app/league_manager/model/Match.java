package com.fifa_app.league_manager.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Club clubPlayingHome;
    private Club clubPlayingAway;
    private String stadium;
    private Instant matchDatetime;
    private Status actualStatus;

    //@JsonIgnore
    //private List<PlayerMatch> playerMatches;

    @JsonIgnore
    private Season season;
}
