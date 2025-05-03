package com.fifa_app.league_manager.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ClubStatistics {
    private Club club;

    @JsonIgnore
    private Year seasonYear;

    @JsonProperty("scoredGoals")
    private int getScoredGoals() {
        List<Goal> clubGoals = new ArrayList<>();

        if (this.getClub().getClubMatches().isEmpty()) {
            return 0;
        }

        this.getClub().getClubMatches().forEach(clubMatch -> {
            if (clubMatch.getMatch().getSeason().getYear().equals(this.seasonYear)) {
                clubMatch.getGoals().forEach(goal -> {
                    if (!goal.isOwnGoal()) {
                        clubGoals.add(goal);
                    }
                });
            }
        });

        return clubGoals.size();
    }

    @JsonProperty("cleanSheets")
    private int getCleanSheets() {
        if (this.getClub().getClubMatches().isEmpty()) {
            return 0;
        }

        List<ClubMatch> matches = this.getClub().getClubMatches().stream().map(clubMatch -> {
            if (clubMatch.getMatch().getSeason().getYear().equals(this.seasonYear)) {
                if (clubMatch.getGoals().isEmpty()) {
                    return clubMatch;
                }
                return null;
            }
            return null;
        }).filter(Objects::nonNull).toList();

        return matches.size();
    }
}
