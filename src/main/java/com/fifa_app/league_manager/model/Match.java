package com.fifa_app.league_manager.model;


import lombok.*;

import java.time.Instant;

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
}
