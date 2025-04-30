package com.fifa_app.league_manager.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Club {
    private String id;
    private String name;
    private long yearCreation;
    private String acronym;
    private String stadium;
    private Coach coach;
}
