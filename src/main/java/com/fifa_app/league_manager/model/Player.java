package com.fifa_app.league_manager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor@NoArgsConstructor@Getter
@Setter@EqualsAndHashCode
public class Player {
    private String name;
    private String id;
    private int number;
    private Positions position;
    private String country;
    private int age;
    @JsonIgnore
    private List<PlayerClub> clubs = new ArrayList<PlayerClub>();
    @JsonProperty("club")
    public Club getActualClub(){
        return clubs.stream().filter(playerClub -> playerClub.getEndDate() == null).toList().get(0).getClub();
    }
}
