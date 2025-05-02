package com.fifa_app.league_manager.model;

import lombok.*;

import java.time.Year;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Season {
    private String id;
    private Year year;
    private String alias;
    private SeasonStatus status;
}
