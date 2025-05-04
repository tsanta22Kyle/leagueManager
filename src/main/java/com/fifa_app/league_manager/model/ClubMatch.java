package com.fifa_app.league_manager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@EqualsAndHashCode
@Getter
@ToString
public class ClubMatch {
    private String id;
    private Club club;
    @JsonIgnore
    private Match match;
    private List<Goal> goals;

    public ClubMatch(String string, Club club, Match match) {
        this.id = string;
        this.club = club;
        this.match = match;
    }


    public int getScore() {
        return this.goals.size();
    }
}
