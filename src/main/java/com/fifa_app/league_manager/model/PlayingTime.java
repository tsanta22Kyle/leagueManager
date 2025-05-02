package com.fifa_app.league_manager.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Setter
@EqualsAndHashCode
@ToString
public class PlayingTime {
    private String id;
    private int value;

    private DurationUnit unit;
}
