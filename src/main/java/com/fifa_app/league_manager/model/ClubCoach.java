package com.fifa_app.league_manager.model;


import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ClubCoach {
    private String id;
    private Club club;
    private Coach coach;
    private Season season;
    private Instant startDate;
    private Instant endDate;
}
