package com.fifa_app.league_manager.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ClubStatistics {
    private String id;
    private String name;
    private long yearCreation;
    private String acronym;
    private String stadium;

    private int rankingPoints;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Coach coach;

    @JsonIgnore
    private Club club;

    @JsonIgnore
    private Year seasonYear;

    public ClubStatistics(Club club) {
        this.id = club.getId();
        this.name = club.getName();
        this.stadium = club.getStadium();
        this.acronym = club.getAcronym();
        this.yearCreation = club.getYearCreation();
    }

    @JsonProperty("scoredGoals")
    private int getScoredGoals() {
        List<Goal> clubGoals = new ArrayList<>();

        if (this.getClub().getClubMatches().isEmpty()) {
            return 0;
        }

        this.getClub().getClubMatches().forEach(clubMatch -> {
            if (clubMatch.getMatch().getSeason().getYear().equals(this.seasonYear) && !clubMatch.getMatch().getActualStatus().equals(Status.NOT_STARTED)) {
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
            if (clubMatch.getMatch().getSeason().getYear().equals(this.seasonYear) && clubMatch.getMatch().getActualStatus().equals(Status.FINISHED)) {
                if (clubMatch.getGoals().isEmpty()) {
                    return clubMatch;
                }
                return null;
            }
            return null;
        }).filter(Objects::nonNull).toList();

        return matches.size();
    }

    @JsonProperty("concededGoals")
    private int getConcededGoals() {
        List<Integer> clubGoals = new ArrayList<>();

        if (this.getClub().getClubMatches().isEmpty()) {
            return 0;
        }

        List<ClubMatch> clubMatchesMatchingTheSeasonYearAndFinishedMatch = this.getClub().getClubMatches().stream()
                .filter(clubMatch -> clubMatch.getMatch().getSeason().getYear().equals(seasonYear))
                .filter(clubMatch -> clubMatch.getMatch().getActualStatus().equals(Status.FINISHED))
                .toList();

        clubMatchesMatchingTheSeasonYearAndFinishedMatch.forEach(clubMatch -> {
            if (clubMatch.getMatch().getClubPlayingHome().getClub().getId().equals(this.getClub().getId())) {
                clubGoals.add(clubMatch.getMatch().getClubPlayingAway().getGoals().size());
            } else if (clubMatch.getMatch().getClubPlayingAway().getClub().getId().equals(this.getClub().getId())) {
                clubGoals.add(clubMatch.getMatch().getClubPlayingHome().getGoals().size());
            }
        });

        return clubGoals.stream().reduce(0, Integer::sum);
    }

    @JsonProperty("differenceGoals")
    private int getDifferenceGoals(){
        return this.getScoredGoals() - this.getConcededGoals();
    }
}
