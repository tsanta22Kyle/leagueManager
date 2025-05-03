package com.fifa_app.league_manager.endpoint.rest;

import com.fifa_app.league_manager.model.Status;
import lombok.*;

import java.time.Instant;
@AllArgsConstructor@NoArgsConstructor@Setter@Getter@ToString@EqualsAndHashCode
public class MatchRest {
    private String id;
    private ClubMatchRest clubPlayingHome;
    private ClubMatchRest clubPlayingAway;
    private String stadium;
    private Instant matchDateTime;
    private Status actualStatus;
}
