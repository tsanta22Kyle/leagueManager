package com.fifa_app.league_manager.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Setter
@EqualsAndHashCode
@ToString
public class PlayerMatch {
    private String id;
    private List<Goal> goals = new ArrayList<>();
    private Player player;
    private Match match;
    private PlayingTime playingTime;
}
