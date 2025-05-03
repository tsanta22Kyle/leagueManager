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

    public int getScore() {
        return 0;
    }
}
