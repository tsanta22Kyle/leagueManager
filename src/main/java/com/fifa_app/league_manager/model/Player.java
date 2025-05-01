package com.fifa_app.league_manager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Player {
    private String name;
    private String id;
    private Positions position;
    private String country;
    private int age;
    private int preferredNumber;
    @JsonIgnore
    private List<PlayerClub> clubs = new ArrayList<PlayerClub>();

    @JsonProperty("club")
    public Club getActualClub() {
        if (!clubs.isEmpty()) {
            return clubs.stream().filter(playerClub -> playerClub.getEndDate() == null).toList().get(0).getClub();
        }
        return null;
    }

    public int getActualNumber() {
        if (!clubs.isEmpty()) {
            return clubs.stream().filter(playerClub -> playerClub.getEndDate() == null).toList().get(0).getNumber();
        }
        return preferredNumber;
    }

    public PlayerClub endContract() {
        if (!clubs.isEmpty()) {
            clubs.forEach(playerClub -> playerClub.setEndDate(LocalDate.now()));
            return clubs.stream().filter(playerClub -> playerClub.getEndDate() == null).toList().get(0);
        }
        return null;
    }

    //
}
