package com.fifa_app.league_manager.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor@NoArgsConstructor@Setter@EqualsAndHashCode@Getter@ToString
public class ClubMatch {
    private String id;
    private Club club;
    private Match match;
    private List<Goal> goals = new ArrayList<Goal>();
    public int getScore(){
        return 0;
    }
}
