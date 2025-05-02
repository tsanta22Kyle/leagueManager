package com.fifa_app.league_manager.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode
@ToString
public class Scorer {
    private String id;
    private PlayerMatch playerMatch;
    private Match match;
    private Club club;
    private Goal goal;
}
