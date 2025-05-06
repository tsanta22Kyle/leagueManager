package com.fifa_app.league_manager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.time.Year;
import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Player {
    private String name;
    private String id;
    private Positions position;
    private String country;
    private int age;
    @JsonIgnore
    private int preferredNumber;
    @JsonIgnore
    private List<PlayerClub> clubs = new ArrayList<>();
    @JsonIgnore
    private List<PlayerMatch> matches = new ArrayList<>();

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

    @JsonProperty("number")
    public int getNumber() {
        if (clubs.size() > 0) {
                return getClubs().stream().filter(playerClub -> playerClub.getEndDate()==null).toList().get(0).getNumber();
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

    @JsonIgnore
    public int getScoreGoals() {
        List<Goal> playerGoals = new ArrayList<>();

        this.matches.forEach(playerMatch -> {
            playerMatch.getGoals().forEach(goal -> {
                if (!goal.isOwnGoal()) {
                    playerGoals.add(goal);
                }
            });
        });

        return playerGoals.size();
    }

    @JsonIgnore
    public PlayingTime getPlayingTime(Year seasonYear) {
        PlayingTime playingTime = new PlayingTime();

        List<PlayingTime> playerPlayingTimes = this.matches.stream().map(playerMatch -> {
                    if (playerMatch.getMatch().getSeason().getYear().equals(seasonYear)) {
                        return playerMatch.getPlayingTime();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .map(plt -> {
                    switch (plt.getUnit()) {
                        case HOUR -> {
                            plt.setValue(plt.getValue() * 60);
                            plt.setUnit(DurationUnit.MINUTE);
                        }
                        case SECOND -> {
                            plt.setValue(Math.round((float) plt.getValue() / 60));
                            plt.setUnit(DurationUnit.MINUTE);
                        }
                    }
                    return plt;
                })
                .toList();

        int playingTimeValue = playerPlayingTimes.stream().map(PlayingTime::getValue).reduce(0, Integer::sum);

        playingTime.setUnit(DurationUnit.MINUTE);
        playingTime.setValue(playingTimeValue);

        return playingTime;
    }


    //
}
