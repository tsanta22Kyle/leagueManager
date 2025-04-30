package com.fifa_app.league_manager.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
@AllArgsConstructor@NoArgsConstructor@Getter@Setter@EqualsAndHashCode@ToString
public class PlayerClub {
    private String id;
    private Club club;
    private LocalDate joinDate;
    private LocalDate endDate;
}
