package com.fifa_app.league_manager.model;

import lombok.*;

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
}
