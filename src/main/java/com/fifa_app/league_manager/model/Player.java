package com.fifa_app.league_manager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor@NoArgsConstructor@Getter
@Setter@EqualsAndHashCode@ToString
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
        if (clubs == null || clubs.isEmpty()) return null;
        Optional<Club> actualClub = clubs.stream()
                .filter(pc -> pc != null && pc.getEndDate() == null)
                .map(PlayerClub::getClub)
                .findFirst();

        //System.out.println("actualCub "+actualClub);
        if (actualClub.isPresent()) {
            return actualClub.get();
        }
        return clubs.stream()
                .filter(pc -> pc != null && pc.getEndDate() != null)
                .max(Comparator.comparing(PlayerClub::getEndDate))
                .map(PlayerClub::getClub)
                .orElse(null);
    }


    public int getActualNumber(){
        if (clubs.size() > 0){

        return clubs.stream().filter(playerClub -> playerClub.getEndDate() == null).toList().get(0).getNumber();
        }
        return preferredNumber;
    }
    public PlayerClub endContract() {
        if (this.getClubs() == null || this.getClubs().isEmpty()) {
            return null;
        }

        for (PlayerClub playerClub : this.getClubs()) {
            if (playerClub.getEndDate() == null) {
                playerClub.setEndDate(LocalDate.now());
                return playerClub;
            }
        }
        return this.getClubs().stream().max((o1, o2) -> o1.getEndDate().compareTo(o2.getEndDate())).get();
    }



    //
}
