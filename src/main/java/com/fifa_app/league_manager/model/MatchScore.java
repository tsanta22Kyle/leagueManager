package com.fifa_app.league_manager.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Setter
@EqualsAndHashCode
@ToString
public class MatchScore {
    private String id;
    private int home;
    private int away;

    private Match match;
}
