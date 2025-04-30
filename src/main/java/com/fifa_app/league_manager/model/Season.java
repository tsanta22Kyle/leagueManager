package com.fifa_app.league_manager.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Season {
    private String id;
    private long year;
    private String alias;
    private SeasonStatus status;
}
