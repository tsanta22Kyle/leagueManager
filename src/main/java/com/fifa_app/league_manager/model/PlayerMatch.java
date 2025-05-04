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

    private Player player;
    private Match match;
    private PlayingTime playingTime;

    private List<Goal> goals;

    public PlayerMatch(String string, Player player, Match match, PlayingTime pt) {
        this.id = string;
        this.player = player;
        this.match = match;
        this.playingTime = pt;
    }
}
