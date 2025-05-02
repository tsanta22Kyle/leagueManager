package com.fifa_app.league_manager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @JsonIgnore
    private List<ClubParticipation> seasonsParticipation = new ArrayList<>();

    public Season getActiveSeason() {
        if (!seasonsParticipation.isEmpty() && this.seasonsParticipation == null) return null;
          //  System.out.println("seasonsParticipation: " + seasonsParticipation);

        Optional<ClubParticipation> actualPreSeason = seasonsParticipation.stream().min((o1, o2) -> o1.getSeason().getYear().compareTo(o2.getSeason().getYear())).stream().findFirst();
        ClubParticipation activeSeason = seasonsParticipation
                .stream()
                .filter(season -> season.getSeason().getStatus() == SeasonStatus.STARTED)
                .findFirst()
                .orElse(actualPreSeason.orElse(null));
    if (activeSeason == null) return null;
        return activeSeason.getSeason();
    }
}
