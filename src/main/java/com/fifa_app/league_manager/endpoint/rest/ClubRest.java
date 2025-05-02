package com.fifa_app.league_manager.endpoint.rest;

import com.fifa_app.league_manager.model.Coach;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ClubRest {
    private String id;
    private String name;
    private long yearCreation;
    private String acronym;
    private String stadium;
    private Coach coach;
}
