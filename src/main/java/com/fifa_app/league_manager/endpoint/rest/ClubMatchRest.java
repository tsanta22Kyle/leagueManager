package com.fifa_app.league_manager.endpoint.rest;

import com.fifa_app.league_manager.model.Goal;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor@NoArgsConstructor@Getter@Setter@ToString@EqualsAndHashCode
public class ClubMatchRest {
    private String id;
    private String name;
    private String acronym;
    private int score;
    private List<ScorerRest> scorers = new ArrayList<>();
    // scorers
}
